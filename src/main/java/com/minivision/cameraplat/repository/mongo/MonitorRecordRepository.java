package com.minivision.cameraplat.repository.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.minivision.cameraplat.domain.record.MonitorRecord;
import com.minivision.cameraplat.rest.param.alarm.AlarmParam;


public interface MonitorRecordRepository extends MongoRepository<MonitorRecord, String> {

  Page<MonitorRecord> findWithParam(AlarmParam param);
  
  int deleteBySnapshotCameraIdAndSnapshotTimestampLessThan(Long camerdId, long timestamp);
}
