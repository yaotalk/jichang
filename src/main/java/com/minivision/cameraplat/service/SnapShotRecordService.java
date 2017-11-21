package com.minivision.cameraplat.service;

import com.minivision.cameraplat.domain.record.SnapshotRecord;
import com.minivision.cameraplat.rest.param.alarm.SnapShotParam;
import com.minivision.cameraplat.rest.result.PageResult;

public interface SnapShotRecordService {

  PageResult<SnapshotRecord> findByTimeandCameraId(SnapShotParam param);
}
