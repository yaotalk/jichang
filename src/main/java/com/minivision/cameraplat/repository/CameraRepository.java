package com.minivision.cameraplat.repository;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Region;
import com.minivision.cameraplat.domain.Strategy;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CameraRepository extends PagingAndSortingRepository<Camera, Long> {
  List<Camera> findAll();

  Set<Camera> findByIdIn(Collection<Long> id);

  List<Camera> findByRegion(Region region);

  List<Camera> findByStrategy(Strategy strategy);

  List<Camera> findByfaceSetsToken(String token);

}
