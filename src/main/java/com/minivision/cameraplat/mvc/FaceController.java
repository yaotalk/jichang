package com.minivision.cameraplat.mvc;

import com.minivision.cameraplat.config.OpAnnotation;
import com.minivision.cameraplat.domain.Face;
import com.minivision.cameraplat.domain.FaceSet;
import com.minivision.cameraplat.domain.FaceTmp;
import com.minivision.cameraplat.mvc.ex.ServiceException;
import com.minivision.cameraplat.param.PageParam;
import com.minivision.cameraplat.service.FaceService;
import com.minivision.cameraplat.service.FaceSetService;
import com.minivision.cameraplat.task.*;
import com.minivision.cameraplat.task.BatchRegistTask.Record;
import com.minivision.cameraplat.task.checkthread.CheckRepeatTaskContext;
import com.minivision.cameraplat.util.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/face")
public class FaceController {
    private static final Logger log = LoggerFactory.getLogger(FaceController.class);
    @Autowired
    private FaceSetService faceSetService;
    @Autowired
    private FaceService faceService;

    @Autowired
    private BatchTaskContext taskContext;

    @Autowired
    private CheckRepeatTask checkRepeatTask;

    @Autowired
    private CheckRepeatTaskContext checkRepeatTaskContext;

    @Autowired
    private MD5InitTask md5InitTask;

    @Value("${system.store}")
    private String filepath;

    @Value("${backup.rarPath}")
    private String rarPath;

    @Value("${system.batch}")
    private String batchPath;

    @Autowired
    private ExImportTask exImportTask;

    @GetMapping()
    public ModelAndView pageinfo() {
        List<FaceSet> faceSets = faceSetService.findByFaceplat();
        return new ModelAndView("faceset/faceadd").addObject("faceSets", faceSets);
    }

    @PostMapping
    @OpAnnotation(modelName = "人脸操作", opration = "新增人脸")
    public String addFace(Face face, @RequestParam("myfile") MultipartFile mfile,
                          @RequestParam("facesetToken") String facesetToken) {
        try {
            String fileType =
                    mfile.getOriginalFilename().substring(mfile.getOriginalFilename().lastIndexOf("."));
            faceService.save(face, facesetToken, mfile.getBytes(), fileType);
            return "success";
        } catch (ServiceException | IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @DeleteMapping
    @OpAnnotation(modelName = "人脸操作", opration = "删除人脸")
    public String delFace(@RequestParam(name = "faceTokens", defaultValue = "") String faceTokens,
                          @RequestParam(name = "facesetToken", defaultValue = "") String facesetToken) {
        faceService.delete(facesetToken, faceTokens);
        return "success";

    }

    @PostMapping("/saveTmp")
    public String updateFace(FaceTmp facetmp) {
        List<FaceTmp> list = new ArrayList<FaceTmp>();
        if (facetmp.getIsStore() == 1) {
            if (!faceService.isCanSaveStore(facetmp)) {
                return "{\"ret\":\"already\"}";
            }
        }
        list.add(facetmp);
        faceService.saveTmp(list);
        return "{\"ret\":\"success\"}";
    }

    @GetMapping("/batch")
    public ModelAndView batchRegist() {
        BatchTask task = taskContext.getCurrentTask();
        ModelAndView mav = new ModelAndView("faceset/batch");
        if (task != null) {
            mav.addObject("task", task);
        }
        return mav;
    }

    @PatchMapping
    @OpAnnotation(modelName = "人脸操作", opration = "编辑人脸")
    public String updateFace(Face face,
                             @RequestParam(name = "facesetToken", defaultValue = "") String facesetToken) {
        faceService.update(face);
        return "success";
    }

    @RequestMapping("/checkIfHasNotDetected")
    public ModelAndView checkIfHasNotDetected(PageParam pageParam, String faceSetToken, RedirectAttributes redirectAttributes) {
        FaceSet faceSet = faceSetService.find(faceSetToken);
        String taskId = UUID.randomUUID().toString();
        checkRepeatTaskContext.setMd5InitTaskId(taskId);
        checkRepeatTaskContext.setDataIniting(true);
        checkRepeatTaskContext.startDeal(faceSetToken);
        checkRepeatTaskContext.setFaceSetName(faceSet.getName());
        return new ModelAndView("redirect:checkRepeat");
    }


    @RequestMapping("/queryRepeat")
    public ModelAndView queryRepeat(PageParam pageParam, String faceSetToken) {
        if (null == faceSetToken) {
            return new ModelAndView("faceset/checkrepeat");
        }
        FaceSet faceSet = faceSetService.find(faceSetToken);
        ModelAndView mav = null;
        int totalCount = checkRepeatTask.getTotalNum();
        if (totalCount == 0) {
            mav = new ModelAndView("faceset/checkrepeat");
            String checkRepeatId = UUID.randomUUID().toString();
            checkRepeatTask.setTaskId(checkRepeatId);
            checkRepeatTask.setFaceSetToken(faceSetToken);
            checkRepeatTask.setContinued(true);
            checkRepeatTask.setTaskStatus(BatchTask.PREPARED);
            checkRepeatTask.setFaceSetName(faceSet.getName());
            mav.addObject("checkRepeatTask", checkRepeatTask);
            mav.addObject("taskStatus", checkRepeatTask.getTaskStatus());
            mav.addObject("checkRepeatId", checkRepeatId);
            return mav;
        }
        mav = new ModelAndView("faceset/checkrepeat");
        String taskId = checkRepeatTask.getTaskId();
        mav.addObject("total", faceService.getToProcessNum(taskId, pageParam));
        mav.addObject("rows", faceService.getToProcess(taskId, pageParam, true, true));
        mav.addObject("faceSetToken", faceSetToken);
        mav.addObject("faceset", faceSet);
        return mav;
    }

    @PostMapping("/cancelCheckRepeat")
    public ModelAndView cancelCheckRepeat() {
        checkRepeatTask.setQueryDataFinished(false);
        faceService.deleteTaskData(checkRepeatTask.getTaskId());
        checkRepeatTask.setTaskId(null);
        return new ModelAndView("redirect:checkRepeat");
    }

    @GetMapping("/checkRepeat")
    public ModelAndView checkRepeat() {
        ModelAndView mav = null;

        if (checkRepeatTaskContext.isDataIniting()) {
            mav = new ModelAndView("faceset/checkrepeatInitial");
            mav.addObject("checkRepeatTaskContext", checkRepeatTaskContext);
            if (md5InitTask.getTaskStatus() != BatchTask.DONE) {
                mav.addObject("task", md5InitTask);
            } else {
                mav.addObject("task", checkRepeatTask);
            }
            mav.addObject("md5InitTaskId", checkRepeatTaskContext.getMd5InitTaskId());
            return mav;
        }
        mav = new ModelAndView("faceset/checkrepeat");
        if (checkRepeatTask.getTaskId() == null) {
            mav.addObject("checkRepeatId", "-1");
            mav.addObject("taskStatus", "-1");
        } else {
            mav.addObject("checkRepeatTask", checkRepeatTask);
            mav.addObject("taskStatus", checkRepeatTask.getTaskStatus());
            mav.addObject("checkRepeatId", checkRepeatTask.getTaskId());
            int totalCount = checkRepeatTask.getTotalNum();
            if (checkRepeatTask.isQueryDataFinished() && totalCount > 0) {
                mav = new ModelAndView("faceset/checkRepeatList");
                mav.addObject("total", totalCount);
                String faceSetToken = checkRepeatTask.getFaceSetToken();
                PageParam pageParam = new PageParam();
                pageParam.setOffset(0);
                pageParam.setLimit(20);
                mav.addObject("rows", faceService.getToProcess(checkRepeatTask.getTaskId(), pageParam, true));
                mav.addObject("faceSetToken", faceSetToken);
                mav.addObject("facesetName", checkRepeatTask.getFaceSetName());
                mav.addObject("checkRepeatId", checkRepeatTask.getTaskId());
            }
        }

        return mav;
    }

    @GetMapping("/batch/error")
    @ResponseBody
    public Map<String, Object> error(int offset, int limit) {
        BatchRegistTask task = (BatchRegistTask) taskContext.getCurrentTask();
        Map<String, Object> res = new HashMap<>();
        if (task != null) {
            List<Record> records = task.getErrorRecords();
            res.put("total", records.size());
            res.put("rows", records.subList(offset, Math.min(offset + limit, records.size())));
        }
        return res;
    }

    @GetMapping("/diskRoot")
    @ResponseBody
    public List<String> getDiskRoot() {
        return CommonUtils.getDiskRoot();
    }

    @GetMapping("/listDir")
    @ResponseBody
    public List<String> listDir(String parent) {
        return CommonUtils.listDir(parent);
    }

    @GetMapping("/getTask")
    @ResponseBody
    public BatchTask getTask() {
        return taskContext.getCurrentTask();
    }

    @GetMapping("/getCheckRepeatTask")
    @ResponseBody
    public CheckRepeatTask getCheckRepeatTask() {
        return checkRepeatTask;
    }


    @PostMapping("/createTask")
    public ModelAndView createTask(String path, String type, String faceSetToken, HttpServletRequest request,
                                   RedirectAttributes redirectAttributes, @RequestParam(value = "myfile", required = false) MultipartFile file) {

        String imgPath = "";
        FaceSet faceSet = faceSetService.find(faceSetToken);
        if ("local".equals(type)) {
            if (null == file) {
                redirectAttributes.addFlashAttribute("createTask", false);
                return new ModelAndView("redirect:batch");
            } else {
                try {
                    File fileDir = new File(batchPath + File.separator + "batch");
                    if (fileDir.exists() && fileDir.isDirectory()) {
                        FileUtils.deleteQuietly(fileDir);
                    }
                    fileDir.mkdirs();
                    File batchFile = new File(batchPath + File.separator + "batch.zip");
                    if (batchFile.exists()) {
                        FileUtils.deleteQuietly(batchFile);
                    }
                    file.transferTo(batchFile);
//                    String oriPath = batchFile.getAbsolutePath();
//                    String unzip = rarPath + "  x -y -ibck " + oriPath + " " + fileDir.getAbsolutePath() + File.separator;
//                    Process process = Runtime.getRuntime().exec(unzip);
//                    if (process.waitFor() != 0) {
//                        log.error("解压失败");
//                        throw new Exception("解压失败");
//                    }
//                    FileUtils.deleteQuietly(batchFile);
//                    List<String> paths = CommonUtils.listDir(fileDir.getPath());
//                    if (null != paths && paths.size() > 0) {
//                        imgPath = paths.get(0);
//                    }
                    exImportTask.Unzip(batchFile,fileDir,faceSet,request.getUserPrincipal().getName());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
                return new ModelAndView("redirect:batch");
            }
        } else {
            if ("".equals(path) || !new File(path).isDirectory()) {
                redirectAttributes.addFlashAttribute("createTask", false);
                return new ModelAndView("redirect:batch");
            }
            imgPath = path;
        }
        BatchRegistTask task = taskContext
                .createBatchRegistTask(faceSet, request.getUserPrincipal().getName(), new File(imgPath));
        try {
            taskContext.submitTask(task);
            redirectAttributes.addFlashAttribute("createTask", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("createTask", false);
        }
        return new ModelAndView("redirect:batch");
    }

    @PostMapping("/startCheckRepeat")
    @OpAnnotation(modelName = "人脸操作", opration = "图片去重")
    public ModelAndView startCheckRepeat(String faceSetToken, HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        checkRepeatTask.setQueryDataFinished(false);
        Thread checkRepeatThread = new Thread(checkRepeatTask);
        checkRepeatThread.start();
        redirectAttributes.addFlashAttribute("checkRepeatId", checkRepeatTask.getTaskId());

        return new ModelAndView("redirect:checkRepeat");
    }

    @GetMapping("/startInitMD5Thread")
    public void startInitMD5Thread() {
        new Thread(md5InitTask).start();
    }


    @MessageMapping("/c/stop/")
    public void receive() {
        taskContext.getCurrentTask().setContinued(false);
    }

    @MessageMapping("/c/stopcheckrepeat/")
    public void receiveStopRepeat() {
        checkRepeatTask.setContinued(false);
    }

    @MessageMapping("/c/stopInitMD5/")
    public void receiveStopInitMD5() {
        checkRepeatTaskContext.stopInitalData();
    }

    @GetMapping("/getTaskStatus")
    @ResponseBody
    public Map<String, Object> getTaskStatus() {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("dataIniting", checkRepeatTaskContext.isDataIniting());
        return res;
    }


}
