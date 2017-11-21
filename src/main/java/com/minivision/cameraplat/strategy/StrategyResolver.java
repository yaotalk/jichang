package com.minivision.cameraplat.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Face;
import com.minivision.cameraplat.domain.MonitorImage;
import com.minivision.cameraplat.domain.record.MonitorRecord;
import com.minivision.cameraplat.domain.record.MonitorRecord.FaceInfo;
import com.minivision.cameraplat.domain.record.SnapshotRecord;
import com.minivision.cameraplat.repository.mongo.MonitorRecordRepository;
import com.minivision.cameraplat.repository.mongo.SnapshotRecordRepository;
import com.minivision.cameraplat.service.FastDFSService;

public abstract class StrategyResolver {

    @Autowired
    private MonitorRecordRepository monitorRepository;
    @Autowired
    private SnapshotRecordRepository snapshotRespository;
    @Autowired
    private SimpMessagingTemplate template;

    /*@Value("${system.store.snapshot}")
    private String imageFilePath = ".";*/

    private String imageFileType = "jpg";
    
    @Autowired
    private FastDFSService fileService;

    protected void saveMonitorImage(MonitorImage image){
        if(image.getFileName() == null){
            //String fileName = image.getSerialNo() + "_" + image.getCameraId() + "_" + getDatePath() + "." + imageFileType;
            //String fileName = "camera_"+image.getCameraId() + "/" + CommonUtils.getDatePath() + "/" + image.getSerialNo() + "." + imageFileType;
            //File imgFile = new File(imageFilePath, fileName);
            String fileName = image.getSerialNo() + "." + imageFileType;
            try {
                //FileUtils.touch(imgFile);
                //FileUtils.writeByteArrayToFile(imgFile, image.getImage());
                image.setFileName(fileService.uploadToFastDFS(fileName, image.getImage()));
                image.setFileUrl(fileService.getFileUrl(image.getFileName()));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected void saveSnapShot(MonitorImage image, SnapshotRecord snapshotRecord){
        saveMonitorImage(image);
        snapshotRecord.setPhotoFileName(image.getFileName());
        snapshotRecord.setPhotoUrl(image.getFileUrl());
        snapshotRespository.save(snapshotRecord);
        template.convertAndSend("/c/snapshot", snapshotRecord);
    }

    protected void saveAlarm(SnapshotRecord snapshotRecord, Face face){
        MonitorRecord record = new MonitorRecord(snapshotRecord);
        if(face!=null){
          FaceInfo faceInfo = new FaceInfo(face);
          record.setFace(faceInfo);
        }
        monitorRepository.save(record);
        template.convertAndSend("/c/alarm", record);
    }

    public abstract void onRecognized(Camera camera, MonitorImage image, SnapshotRecord snapshotRecord, String faceToken);

    public abstract void onStranger(Camera camera, MonitorImage image, SnapshotRecord snapshotRecord);

}
