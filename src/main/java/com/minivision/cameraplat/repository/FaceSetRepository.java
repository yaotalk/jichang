package com.minivision.cameraplat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.cameraplat.domain.FaceSet;

import java.util.List;

public interface FaceSetRepository extends PagingAndSortingRepository<FaceSet, String> {
    List<FaceSet> findAll();
}
