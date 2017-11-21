package com.minivision.cameraplat.repository.mongo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.minivision.cameraplat.repository.mongo.MonitorRecordRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@DataMongoTest
public class RecordRepositoryTest {
  @Autowired
  private MonitorRecordRepository monitorRecordRepository;
  
  @Test
  public void test(){
    int num = monitorRecordRepository.deleteBySnapshotCameraIdAndSnapshotTimestampLessThan(1l, 1501556714818l);
    
    System.out.println(num);
  }
}
