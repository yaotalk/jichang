package com.minivision.cameraplat.service;

import com.minivision.cameraplat.domain.record.MonitorRecord;
import com.minivision.cameraplat.rest.param.alarm.AlarmParam;
import com.minivision.cameraplat.rest.result.PageResult;

public interface MonitorRecordService {
    PageResult<MonitorRecord> findMonitorRecords(AlarmParam param);
}
