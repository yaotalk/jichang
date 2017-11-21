package com.minivision.cameraplat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.cameraplat.domain.Scheme;
import com.minivision.cameraplat.domain.Strategy;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
public interface StrategyRepository extends PagingAndSortingRepository<Strategy, Long> {
    List<Strategy> findAll();

    List<Strategy> findByScheme(Scheme scheme);
}
