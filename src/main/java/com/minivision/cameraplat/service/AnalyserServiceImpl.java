package com.minivision.cameraplat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.cameraplat.domain.Analyser;
import com.minivision.cameraplat.domain.AnalyserStatus;
import com.minivision.cameraplat.repository.AnalyserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AnalyserServiceImpl implements AnalyserService {

  private final Map<Long, Analyser> onlineAnalyser = new HashMap<>();

  @Autowired
  private AnalyserRepository analyserRepository;

  @Autowired
  private AnalyserStatusServiceImpl statusService;

  public List<Analyser> findAll() {
    return analyserRepository.findAll();
  }

  @Override
  public List<AnalyserShow> findAllWithStatus() {
    List<AnalyserShow> list = new ArrayList<AnalyserShow>();
    List<Analyser> analysers = analyserRepository.findAll();
    for (Analyser analyser : analysers) {
      boolean isonline = this.isOnline(analyser.getId());
      AnalyserStatus analyserStatus = statusService.lastStatus(analyser.getId());
      AnalyserShow analyserShow = new AnalyserShow(analyser, analyserStatus, isonline);
      list.add(analyserShow);
    }
    return list;
  }

  @Override
  public Analyser update(Analyser analyser) {
    return analyserRepository.save(analyser);
  }

  public Analyser create(Analyser analyser) {

    return analyserRepository.save(analyser);
  }

  @Override
  public void delete(Analyser analyser) {
    analyserRepository.delete(analyser);
  }

  @Override
  public Page<Analyser> findAnalysers(Pageable pageable) {
    return analyserRepository.findAll(pageable);
  }

  @Override
  public String findPwdByUsername(String username) {
    return analyserRepository.findPasswordByUsername(username);
  }

  @Override
  public Analyser fingByUsername(String username) {
    return analyserRepository.findByUsername(username);
  }

  @Override
  public Analyser findById(long id) {
    return analyserRepository.findOne(id);
  }

  @Override
  public List<Analyser> getOnlineAnalyser() {
    return new ArrayList<>(onlineAnalyser.values());
  }

  @Override
  public boolean isOnline(Long analyserId) {
    return onlineAnalyser.containsKey(analyserId);
  }

  @Override
  public void online(Long analyserId) {
    onlineAnalyser.putIfAbsent(analyserId, analyserRepository.findOne(analyserId));
  }

  @Override
  public void offline(Long analyserId) {
    onlineAnalyser.remove(analyserId);
  }

  public static class AnalyserShow

  {
    public AnalyserShow(Analyser analyser, AnalyserStatus analyserStatus, boolean isonline) {
      this.analyser = analyser;
      this.analyserStatus = analyserStatus;
      this.isonline = isonline;
    }

    private Analyser analyser;
    private AnalyserStatus analyserStatus;

    public boolean getIsonline() {
      return isonline;
    }

    public void setIsonline(boolean isonline) {
      this.isonline = isonline;
    }

    private boolean isonline;

    public Analyser getAnalyser() {
      return analyser;
    }

    public void setAnalyser(Analyser analyser) {
      this.analyser = analyser;
    }

    public AnalyserStatus getAnalyserStatus() {
      return analyserStatus;
    }

    public void setAnalyserStatus(AnalyserStatus analyserStatus) {
      this.analyserStatus = analyserStatus;
    }

  }

}
