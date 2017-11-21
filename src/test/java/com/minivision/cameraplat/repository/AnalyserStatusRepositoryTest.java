package com.minivision.cameraplat.repository;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.minivision.cameraplat.domain.Analyser;
import com.minivision.cameraplat.domain.AnalyserStatus;
import com.minivision.cameraplat.repository.AnalyserStatusRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@DataMongoTest
public class AnalyserStatusRepositoryTest {

  @Autowired
  private AnalyserStatusRepository statusRepository;
  
  @Test
  public void test(){
    List<AnalyserStatus> status = statusRepository.findByAnalyserId(1);
    
    System.out.println(status.size());
    
    Analyser analyser = new Analyser();
    analyser.setId(1l);
    
    List<AnalyserStatus> s = statusRepository.findByAnalyser(analyser);
    
    System.out.println(s.size());
  }
  
  
}
