package com.minivision.cameraplat.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.cameraplat.domain.Analyser;
import com.minivision.cameraplat.domain.AnalyserStatus;

public interface AnalyserStatusRepository extends PagingAndSortingRepository<AnalyserStatus,Long>{

  List<AnalyserStatus> findByAnalyser(Analyser analyser);

  List<AnalyserStatus> findByAnalyserId(long analyserId);

  List<AnalyserStatus> findByAnalyserOrderByTimestampAsc(Analyser analyser);
}
