package com.minivision.cameraplat.service;

import java.util.List;

import com.minivision.cameraplat.domain.AnalyserStatus;

public interface AnalyserStatusService {
  AnalyserStatus save(AnalyserStatus status);
  List<AnalyserStatus> findByAnalyser(long analyserId);
  AnalyserStatus lastStatus(long analyserId);
}
