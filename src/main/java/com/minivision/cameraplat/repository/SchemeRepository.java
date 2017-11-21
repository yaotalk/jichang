package com.minivision.cameraplat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.cameraplat.domain.Scheme;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
public interface SchemeRepository extends PagingAndSortingRepository<Scheme, Long>{
        List<Scheme> findAll();
        void deleteByIdIn(List<Long> id);
}
