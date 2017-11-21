package com.minivision.cameraplat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Scheme;
import com.minivision.cameraplat.domain.Strategy;
import com.minivision.cameraplat.repository.CameraRepository;
import com.minivision.cameraplat.repository.StrategyRepository;

import java.util.List;

@Service
@Transactional
public class StrategyServiceImpl implements StrategyService {
  @Autowired
  private StrategyRepository strategyRepository;

  @Autowired
  private CameraRepository cameraRepository;

  @Override
  public List<Strategy> findAll() {
    return strategyRepository.findAll();
  }

  @Override
  public Strategy create(Strategy strategy) {
    return strategyRepository.save(strategy);
  }

  @Override
  public Strategy update(Strategy strategy) {
    return strategyRepository.save(strategy);
  }

  @Override
  public void delete(Strategy strategy) {
    List<Camera> cameras = cameraRepository.findByStrategy(strategy);
    for(Camera camera : cameras){
       camera.setStrategy(null);
    }
//    List<Long> ids = new ArrayList<Long>();
//    for (String index : id.split(",")) {
//      ids.add(Long.valueOf(index));
//    }
//    strategyRepository.deleteById(Long.valueOf(id));
    strategyRepository.delete(strategy.getId());
  }

  @Override public List<Strategy> findByScheme(Scheme scheme) {
    return strategyRepository.findByScheme(scheme);
  }

}
