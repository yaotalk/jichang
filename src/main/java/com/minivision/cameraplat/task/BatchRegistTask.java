package com.minivision.cameraplat.task;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minivision.cameraplat.domain.Face;
import com.minivision.cameraplat.domain.FaceSet;


public class BatchRegistTask extends BatchTask{
  
  private static final Logger logger = LoggerFactory.getLogger(BatchRegistTask.class);

  private static final String[] IMAGE_EXTENSION = new String[]{"jpg","jpeg","png","JPG","JPEG"};
  
  private AtomicInteger success = new AtomicInteger();
  private AtomicInteger error = new AtomicInteger();
  private File filePath;
  private FaceSet faceSet;
  
  @JsonIgnore
  private List<Record> errorRecords = new CopyOnWriteArrayList<>();
  
  public BatchRegistTask(File dir, FaceSet faceSet, BatchTaskContext taskContext) {
    this(null, null, dir, faceSet, taskContext);
  }
  
  public BatchRegistTask(String taskId, String creator, File dir, FaceSet faceSet, BatchTaskContext taskContext) {
    super(taskId, creator, taskContext);
    this.filePath = dir;
    this.faceSet = faceSet;
    this.taskContext = taskContext;
  }
  
  @Override
  protected void doTask() {
    try {
      List<File> photos = (List<File>) FileUtils.listFiles(filePath, IMAGE_EXTENSION, false);
      if(photos.size() ==0){
      progress();
      return;
    }
    setTotal(photos.size());
    CountDownLatch latch = new CountDownLatch(photos.size());
    for(File file : photos) {
      if (!isContinued()) {
        break;
      }
      this.taskContext.getWorker().submit(new ImportTask(file, latch));
     }
    }
   catch (RuntimeException e){
     logger.error(e.getMessage());
     progress();
  }

  }
  
  
  public class ImportTask implements Runnable{
    private File file;
    private CountDownLatch latch;
    
    
    public ImportTask(File file, CountDownLatch latch) {
      this.file = file;
      this.latch = latch;
    }

    @Override
    public void run() {
      taskContext.sendLog(getTaskId(), "开始导入"+file.getName()+"...");
      String fileName = file.getName().split("\\.")[0];
      String[] infos = fileName.split("_");
      Face face = new Face();
      face.setName(infos[0].trim());
      if(infos.length>1){
        face.setEmployeeId(infos[1].trim());
      }
      String fileType = file.getName().substring(file.getName().lastIndexOf("."));
      try {
        byte[] bs = FileUtils.readFileToByteArray(file);
        taskContext.getFaceService().save(face, faceSet.getToken(), bs, fileType);
        BatchRegistTask.this.success.incrementAndGet();
        taskContext.sendLog(getTaskId(), "导入"+fileName+"成功");
      } catch (Exception e ) {
        Record record = new Record(fileName, file.getPath(), false);
        record.setContent(e.getMessage());
        /*if(errorRecords.size() == 100){
          errorRecords.remove(0);
        }*/
        errorRecords.add(record);
        BatchRegistTask.this.error.incrementAndGet();
        taskContext.sendLog(getTaskId(), "导入"+file.getName()+"失败。"+e.getMessage());
        logger.warn("导入失败, task:{}, file:{}", getTaskId(), file.getName(), e);
      }finally{
        progress();
        latch.countDown();
      }
      
    }
    
    
  }

  public int getSuccess() {
    return success.get();
  }

  public int getError() {
    return error.get();
  }

  public File getFilePath() {
    return filePath;
  }

  public void setFilePath(File filePath) {
    this.filePath = filePath;
  }

  public FaceSet getFaceSet() {
    return faceSet;
  }

  public void setFaceSet(FaceSet faceSet) {
    this.faceSet = faceSet;
  }
  
  public List<Record> getErrorRecords() {
    return errorRecords;
  }

  public void setErrorRecords(List<Record> errorRecords) {
    this.errorRecords = errorRecords;
  }



  public class Record{
    private Date date;
    private String name;
    private String file;
    private boolean success;
    private String content;
    public Record(String name, String path, boolean success) {
      this.date = new Date();
      this.name = name;
      this.file = path;
      this.success = success;
    }
    public Date getDate() {
      return date;
    }
    public void setDate(Date date) {
      this.date = date;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getFile() {
      return file;
    }
    public void setFile(String file) {
      this.file = file;
    }
    public boolean isSuccess() {
      return success;
    }
    public void setSuccess(boolean success) {
      this.success = success;
    }
    public String getContent() {
      return content;
    }
    public void setContent(String content) {
      this.content = content;
    }
  }
}
