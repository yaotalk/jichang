package com.minivision.cameraplat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.cameraplat.domain.Region;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RegionRepository extends PagingAndSortingRepository<Region, Long> {
    List<Region> findAll();

    Region findOne(long id);

    Set<Region> findByParentNodeIn(Region region);

    List<Region> findByIdNotIn(Collection<Long> ids);
}
