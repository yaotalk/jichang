package com.minivision.cameraplat.repository.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.minivision.cameraplat.domain.record.SnapshotRecord;
public interface SnapshotRecordRepository extends MongoRepository<SnapshotRecord,String>{
    Page<SnapshotRecord> findByCameraIdAndTimestampBetweenOrderByTimestampDesc(long cameraid,long startTime,long endTime,Pageable pageable);
    
    int deleteByCameraIdAndTimestampLessThan(long cameraId, long timestamp);
}
