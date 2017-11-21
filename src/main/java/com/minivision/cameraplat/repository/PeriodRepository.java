package com.minivision.cameraplat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.cameraplat.domain.Scheme;

public interface PeriodRepository extends PagingAndSortingRepository<Scheme.Period,Long> {
}
