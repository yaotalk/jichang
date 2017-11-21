package com.minivision.cameraplat.repository.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.minivision.cameraplat.domain.record.MonitorRecord;
import com.minivision.cameraplat.rest.param.alarm.AlarmParam;
import com.minivision.cameraplat.util.ChunkRequest;

public class MonitorRecordRepositoryImpl{
  
  @Autowired  
  private MongoTemplate mongoTemplate;
  
/*  public MonitorRecordRepositoryImpl(MongoEntityInformation<MonitorRecord, Long> metadata,
      MongoOperations mongoOperations) {
    super(metadata, mongoOperations);
  }*/

  public Page<MonitorRecord> findWithParam(AlarmParam param) {
    Query query = new Query();  
    query.addCriteria(Criteria.where("snapshot.timestamp").gte(param.getStartTime()).lte(param.getEndTime()));
    if(param.getCameraId() != null){
      query.addCriteria(Criteria.where("snapshot.cameraId").is(param.getCameraId()));
    }
    if(param.getFaceToken() != null){
      query.addCriteria(Criteria.where("face.id").is(param.getFaceToken()));
    }
    if(param.getSex() != null){
      query.addCriteria(Criteria.where("face.sex").is(param.getSex()));
    }
    if(param.getName() != null){
      query.addCriteria(Criteria.where("face.name").is(param.getName()));  
    }
    query.with(new Sort(Direction.DESC, "snapshot.timestamp"));
    ChunkRequest pageable = new ChunkRequest(param.getOffset(), param.getLimit(), new Sort(Direction.DESC, "snapshot.timeStamp"));
    query.with(pageable);
    Long count =  mongoTemplate.count(query, MonitorRecord.class);  
    List<MonitorRecord> list = mongoTemplate.find(query, MonitorRecord.class);
    Page<MonitorRecord> pagelist = new PageImpl<MonitorRecord>(list, pageable, count);  
    return pagelist;
  }
  
}
