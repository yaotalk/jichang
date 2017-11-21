package com.minivision.cameraplat.service;

import org.springframework.data.domain.Page;

import com.minivision.cameraplat.domain.Region;
import com.minivision.cameraplat.service.RegionServiceImpl.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RegionService {
    List<Region> findAll();

    Region create(Region region);

    Region findById(long id);

    Region update(Region region);

    void delete(long id);

    Map<String, TreeNode> groupCameraByRegion();

    List<Region> findNotChildren(Long regionid);

    Set<Region> findChildren(Region region);

    Page<Region> findAllWithPage(int page, int size);
}
