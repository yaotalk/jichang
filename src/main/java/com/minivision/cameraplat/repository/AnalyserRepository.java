package com.minivision.cameraplat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.cameraplat.domain.Analyser;

public interface AnalyserRepository extends PagingAndSortingRepository<Analyser,Long>{
  String findPasswordByUsername(String username);
  Analyser findByUsername(String username);
  List<Analyser> findAll();
  List<String> findByname(String name, Pageable page);
}
