package com.minivision.cameraplat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.cameraplat.domain.record.MonitorRecord;
import com.minivision.cameraplat.repository.mongo.MonitorRecordRepository;
import com.minivision.cameraplat.rest.param.alarm.AlarmParam;
import com.minivision.cameraplat.rest.result.PageResult;

@Service
@Transactional
public class MonitorRecordServiceImpl implements MonitorRecordService {

  @Autowired
  private MonitorRecordRepository monitorRecordRepository;
  
  @Override
  public PageResult<MonitorRecord> findMonitorRecords(AlarmParam param) {
    Page<MonitorRecord> page = monitorRecordRepository.findWithParam(param);
    return new PageResult<>(page.getTotalElements(), page.getContent());
  }
}
