package com.minivision.cameraplat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minivision.cameraplat.domain.record.SnapshotRecord;
import com.minivision.cameraplat.repository.mongo.SnapshotRecordRepository;
import com.minivision.cameraplat.rest.param.alarm.SnapShotParam;
import com.minivision.cameraplat.rest.result.PageResult;
import com.minivision.cameraplat.util.ChunkRequest;
@Service
public class SnapShotRecordServiceImpl implements SnapShotRecordService{

    @Autowired
    private SnapshotRecordRepository snapshotRecordRepository;
    
    @Override
    public PageResult<SnapshotRecord> findByTimeandCameraId(SnapShotParam param) {
        Pageable pageable = new ChunkRequest(param.getOffset(), param.getLimit());
        Page<SnapshotRecord> pages = snapshotRecordRepository.findByCameraIdAndTimestampBetweenOrderByTimestampDesc(param.getCameraId(),param.getStartTime(),param.getEndTime(),pageable);
        return new PageResult<SnapshotRecord>(pages.getTotalElements(), pages.getContent());
    }
}
