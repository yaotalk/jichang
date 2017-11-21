package com.minivision.cameraplat.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minivision.cameraplat.domain.record.SnapshotRecord;
import com.minivision.cameraplat.repository.mongo.SnapshotRecordRepository;

@RestController
public class WebSocketController {
  
  @Autowired
  private SnapshotRecordRepository snapshotRecordRepository;
  
  @Autowired
  private SimpMessagingTemplate template;

  @RequestMapping("/send")
  public void test() {
    SnapshotRecord record = snapshotRecordRepository.findOne("597fefffd66a4e0d74092643");
    System.out.println(record);
    template.convertAndSend("/c/snapshot", record);
  }
}
