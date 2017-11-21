package com.minivision.cameraplat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.cameraplat.domain.Scheme;
import com.minivision.cameraplat.repository.PeriodRepository;
import com.minivision.cameraplat.repository.SchemeRepository;

import java.util.List;

@Service
@Transactional
public class SchemeServiceImpl implements SchemeService {

  @Autowired
  private SchemeRepository schemeRepository;

  @Autowired
  private PeriodRepository periodRepository;

  @Override
  public List<Scheme> findAll() {
    return schemeRepository.findAll();
  }

  @Override
  public Scheme findOne(long id) {
    return schemeRepository.findOne(id);
  }

  @Override
  public Scheme create(Scheme scheme) {
    return schemeRepository.save(scheme);
  }

  @Override
  public Scheme update(Scheme scheme) {
    return schemeRepository.save(scheme);
  }

  @Override
  public void delete(long id) {

      Scheme scheme = schemeRepository.findOne(id);
      for (Scheme.Period period : scheme.getPeriod()) {
        periodRepository.delete(period.getId());
      }
      schemeRepository.delete(id);
    }

}
