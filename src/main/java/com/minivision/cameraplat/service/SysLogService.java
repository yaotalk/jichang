package com.minivision.cameraplat.service;


import com.minivision.cameraplat.domain.SysLog;
import com.minivision.cameraplat.param.PageParam;
import org.springframework.data.domain.Page;

import java.text.ParseException;

public interface SysLogService {

    SysLog save(SysLog sysLog);

    Page<SysLog> findByPage(PageParam pageParam,String ModelName,String startTime,String endTime)
        throws ParseException;
}
