package com.minivision.cameraplat.service;
import com.minivision.cameraplat.param.PageParam;
import com.minivision.cameraplat.util.ChunkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.cameraplat.domain.SysLog;
import com.minivision.cameraplat.repository.SysLogRepository;

import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
@Service
@Transactional
public class SysLogServicelmpl implements SysLogService {

    @Autowired
    private SysLogRepository sysLogRepository;

    @Override
    public SysLog save(SysLog sysLog) {
        return sysLogRepository.save(sysLog);
    }

    @Override public Page<SysLog> findByPage(PageParam pageParam,String ModelName,String Time1,String Time2)
        throws ParseException {
        Pageable pageable = new ChunkRequest(pageParam.getOffset(),pageParam.getLimit());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime    = sdf.parse(Time1);
            Date endTime = sdf.parse(Time2);
        return sysLogRepository.findAll((root, criteriaQuery, cb) -> {
            Predicate name = null;
            if (ModelName != null && !"".equals(ModelName)) {
                  name =
                    cb.like(root.get("modelName").as(String.class), "%" + ModelName + "%");
            }
            Predicate time = cb.between(root.get("createTime").as(Date.class),startTime,endTime);
            if(name !=null){
                criteriaQuery.where(name,time);
            }
            else
            {
                criteriaQuery.where(time);
            }
            criteriaQuery.orderBy(cb.desc(root.get("createTime").as(Date.class)));
            return criteriaQuery.getRestriction();
        },pageable);
    }
}
