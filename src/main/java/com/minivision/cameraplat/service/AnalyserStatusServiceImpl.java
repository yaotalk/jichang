package com.minivision.cameraplat.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.cameraplat.domain.AnalyserStatus;
import com.minivision.cameraplat.repository.AnalyserStatusRepository;

@Service
@Transactional
public class AnalyserStatusServiceImpl implements AnalyserStatusService{
  
  @Autowired
  private AnalyserStatusRepository statusRepository;
  
  private HashMap<Long, AnalyserStatus> currentStatus = new HashMap<>();

  @Override
  public AnalyserStatus save(AnalyserStatus status) {
    currentStatus.put(status.getAnalyser().getId(), status);
    return statusRepository.save(status);
  }

  @Override
  public List<AnalyserStatus> findByAnalyser(long analyserId) {
    return statusRepository.findByAnalyserId(analyserId);
  }

  @Override
  public AnalyserStatus lastStatus(long analyserId) {
    return currentStatus.get(analyserId);
  }

}
