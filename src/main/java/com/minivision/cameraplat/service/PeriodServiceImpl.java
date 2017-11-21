package com.minivision.cameraplat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.cameraplat.domain.Scheme;
import com.minivision.cameraplat.repository.PeriodRepository;

@Service
@Transactional
public class PeriodServiceImpl implements PeriodService {

  @Autowired
  private PeriodRepository periodRepository;

  @Override public void delete(Long period ) {
     periodRepository.delete(period);
  }

  @Override public void save(Scheme.Period period) {
       periodRepository.save(period);
  }
}
