package com.minivision.cameraplat.task;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.repository.mongo.MonitorRecordRepository;
import com.minivision.cameraplat.repository.mongo.SnapshotRecordRepository;
import com.minivision.cameraplat.service.CameraService;

@Component
public class ExpireRecordCleaner {
  
  private static final Logger logger = LoggerFactory.getLogger(ExpireRecordCleaner.class);
  
  @Autowired
  private MonitorRecordRepository monitorRecordRepository;
  
  @Autowired
  private SnapshotRecordRepository snapRecordRepository;
  
  @Autowired
  private CameraService cameraService;
  
  @Scheduled(cron = "${system.taskSchedule: 00 00 03 * * ?}")
  public void clean() {
    logger.info("<<==================== expire record cleaner started =================>>");
    List<Camera> cameras = cameraService.findAll();  
    for(Camera camera: cameras){
      int preserveDays = camera.getStrategy().getPreserveDays();
      logger.info("camera: {} ", camera);
      logger.info("preserve days: {}", preserveDays);
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime time = now.minusDays(preserveDays).truncatedTo(ChronoUnit.DAYS);
      Timestamp timestamp = Timestamp.valueOf(time);
      int n1 = snapRecordRepository.deleteByCameraIdAndTimestampLessThan(camera.getId(), timestamp.getTime());
      logger.info("remove [{}] snapshotRecords of camera [{}]", n1, camera.getId());
      int n2 = monitorRecordRepository.deleteBySnapshotCameraIdAndSnapshotTimestampLessThan(camera.getId(), timestamp.getTime());
      logger.info("remove [{}] monitorRecords of camera [{}]", n2, camera.getId());
    }
    logger.info("<<==================== expire record cleaner completed =================>>");
  }
  
}
