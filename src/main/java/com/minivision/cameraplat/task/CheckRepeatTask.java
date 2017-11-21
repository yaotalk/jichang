package com.minivision.cameraplat.task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.minivision.cameraplat.domain.Face;
import com.minivision.cameraplat.domain.FaceSet;
import com.minivision.cameraplat.domain.FaceTmp;
import com.minivision.cameraplat.param.PageParam;
import com.minivision.cameraplat.service.FaceService;
import com.minivision.cameraplat.service.FaceServiceImpl;
import com.minivision.cameraplat.service.FaceSetService;
import com.minivision.cameraplat.task.checkthread.CheckRepeatTaskContext;

// 只允许单任务
@Component
public class CheckRepeatTask implements Runnable {

  private Logger logger = LoggerFactory.getLogger(FaceServiceImpl.class);

  @Autowired
  private SimpMessagingTemplate messageTemplate;

  @Autowired
  private FaceSetService faceSetService;

  @Autowired
  private FaceService faceService;

  private CheckRepeatTaskContext checkRepeatTaskContext;

  /*@Value("${system.store.people}")
  private String basePicPath;*/

  private String taskId;

  private String faceSetToken;

  private String taskName;

  private String faceSetName;

  private int stillRepeatNumber;

  private int totalNum;

  private int processedNum;

  private int taskStatus;

  private boolean continued;

  private boolean queryDataFinished;

  private static final int PAGE_SIZE = 10000;

  private List<String> needDeleteList;

  private int totalPage;

  public void check() {
    processedNum = 0;
    taskName = "findRepeat";
    sendCurrentStatus();
    queryDataFinished = false;
    FaceSet faceSet = faceSetService.find(faceSetToken);
    setFaceSetToken(faceSetToken);
    setContinued(true);
    setTaskStatus(BatchTask.RUNNING);
    setFaceSetName(faceSet.getName());
    int count = faceService.getRepeatFaceCoutBySetToken(faceSetToken);
    totalNum = count;
    if (count == 0) {
      totalPage = 0;
      queryDataFinished = true;
      taskStatus = BatchTask.DONE;
      taskName = "insertReapt";
      messageTemplate.convertAndSend("/w/task/" + taskId, this);
      taskName = "";
      return;
    }

    faceService.deleteTaskData(taskId);

    taskName = "insertReapt";
    checkRepeatTaskContext.setStatusText("找到重复的图片，正在插入待处理表");

    sendCurrentStatus();

    logger.info("重复的图片数量为" + count);
    PageParam pageParam = new PageParam();
    int page = count / 10000 + (count % 10000 == 0 ? 0 : 1);

    totalPage = page;
    // 首先将查询的结果保存到一张临时表中
    for (int i = 0; i < page; i++) {
      pageParam.setOffset(i * 10000);
      pageParam.setLimit(10000);
      List<Face> list = faceService.getRepeatFaceBySetToken(faceSetToken, pageParam);
      List<FaceTmp> listTmp = new ArrayList<FaceTmp>();
      for (Face face : list) {
        listTmp.add(new FaceTmp(face.getId(), taskId));
        // faceService.save(new FaceTmp(face.getId(), taskId));
      }
      faceService.save(listTmp);
      processedNum += listTmp.size();
      sendCurrentStatus();
    }
    queryDataFinished = true;
    sendCurrentStatus();
  }

  @Override
  public void run() {
    processedNum = 0;
    stillRepeatNumber = 0;
    String currentMD5 = null;
    String lastPath = null;
    PageParam pageParam = new PageParam();
    needDeleteList = new ArrayList<String>();
    for (int i = 0; i < totalPage; i++) {


      pageParam.setOffset(i * PAGE_SIZE);
      pageParam.setLimit(PAGE_SIZE);
      List<Face> listFace = faceService.getToProcess(taskId, pageParam, true);

      if (listFace == null || listFace.size() == 0) {
        break;
      }


      for (Face face : listFace) {
        processedNum++;
        if (!face.getPicMd5().equals(currentMD5)) {
          lastPath = face.getImgpath();
          currentMD5 = face.getPicMd5();
        } else {
          /*if (!isSameFile(basePicPath + File.separator + lastPath,
              basePicPath + File.separator + face.getImgpath())) {
            continue;
          }*/
          needDeleteList.add(face.getId());

          if (needDeleteList.size() >= 50) {
            deleteFace();
          }
          if (processedNum % 100 == 0) {
            if (!continued) {
              stopTask();
              break;
            }
            messageTemplate.convertAndSend("/w/task/" + taskId, this);
          }
        }
      }


    }
    if (needDeleteList.size() > 0) {
      deleteFace();
    }
    faceService.deleteTaskData(taskId);
    taskStatus = BatchTask.DONE;
    messageTemplate.convertAndSend("/w/task/" + taskId, this);
  }

  private void deleteFace() {
    StringBuilder sb = new StringBuilder();
    for (String id : needDeleteList) {
      sb.append(id);
      sb.append(",");
    }
    String paraStr = sb.toString();
    try {
      faceService.delete(faceSetToken, paraStr.substring(0, paraStr.length() - 1));
    } catch (Exception e) {
      logger.error("error when delete", e);
      stillRepeatNumber += needDeleteList.size();
    }
    needDeleteList.clear();
  }

  private boolean isSameFile(String fileName1, String fileName2) {
    FileInputStream fis1 = null;
    FileInputStream fis2 = null;
    try {
      fis1 = new FileInputStream(fileName1);
      fis2 = new FileInputStream(fileName2);

      int len1 = fis1.available();// 返回总的字节数
      int len2 = fis2.available();

      if (len1 == len2) {// 长度相同，则比较具体内容
        // 建立两个字节缓冲区
        byte[] data1 = new byte[len1];
        byte[] data2 = new byte[len2];

        // 分别将两个文件的内容读入缓冲区
        fis1.read(data1);
        fis2.read(data2);
        return Arrays.equals(data1, data2);
      } else {
        // 长度不一样，文件肯定不同
        return false;
      }
    } catch (FileNotFoundException e) {
      logger.error("error when compare jpeg file", e);
    } catch (IOException e) {
      logger.error("error when compare jpeg file", e);
    } finally {// 关闭文件流，防止内存泄漏
      if (fis1 != null) {
        try {
          fis1.close();
        } catch (IOException e) {
          // 忽略
          logger.error("error when compare jpeg file", e);
        }
      }
      if (fis2 != null) {
        try {
          fis2.close();
        } catch (IOException e) {
          logger.error("error when compare jpeg file", e);
        }
      }
    }
    return false;
  }

  public void sendCurrentStatus() {
    messageTemplate.convertAndSend("/w/task/" + taskId, this);
  }

  public void stopTask() {
    taskStatus = BatchTask.DONE;
    faceService.deleteTaskData(taskId);
    String tmpTaskId = taskId;
    taskId = null;
    taskName = "stopInit";
    messageTemplate.convertAndSend("/w/task/" + tmpTaskId, this);
    taskName = "";

  }

  public void setFaceSetToken(String faceSetToken) {
    this.faceSetToken = faceSetToken;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getTaskId() {
    return taskId;
  }

  public int getProcessedNum() {
    return processedNum;
  }

  public void setProcessedNum(int processedNum) {
    this.processedNum = processedNum;
  }

  public boolean isContinued() {
    return continued;
  }

  public void setContinued(boolean continued) {
    this.continued = continued;
  }

  public int getTaskStatus() {
    return taskStatus;
  }

  public void setTaskStatus(int taskStatus) {
    this.taskStatus = taskStatus;
  }

  public String getFaceSetName() {
    return faceSetName;
  }

  public void setFaceSetName(String faceSetName) {
    this.faceSetName = faceSetName;
  }

  public int getStillRepeatNumber() {
    return stillRepeatNumber;
  }

  public void setStillRepeatNumber(int stillRepeatNumber) {
    this.stillRepeatNumber = stillRepeatNumber;
  }

  public boolean isQueryDataFinished() {
    return queryDataFinished;
  }

  public void setQueryDataFinished(boolean queryDataFinished) {
    this.queryDataFinished = queryDataFinished;
  }

  public String getFaceSetToken() {
    return faceSetToken;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public CheckRepeatTaskContext getCheckRepeatTaskContext() {
    return checkRepeatTaskContext;
  }

  public void setCheckRepeatTaskContext(CheckRepeatTaskContext checkRepeatTaskContext) {
    this.checkRepeatTaskContext = checkRepeatTaskContext;
  }

  public int getTotalNum() {
    return totalNum;
  }

  public void setTotalNum(int totalNum) {
    this.totalNum = totalNum;
  }

}
