package com.minivision.cameraplat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Region;
import com.minivision.cameraplat.repository.RegionRepository;

import java.util.*;

@Service
@Transactional
public class RegionServiceImpl implements RegionService {

  @Autowired
  private RegionRepository regionRepository;
  
  @Autowired
  CameraService cameraService;

  @Override
  public List<Region> findAll() {
    return regionRepository.findAll();
  }

  @Override
  public Page<Region> findAllWithPage(int page,int size) {
    Pageable pageable = new PageRequest(page,size);
    return regionRepository.findAll(pageable);
  }
  @Override
  public Region create(Region region) {
    Assert.notNull(region, "region must not be null");
    if(region.getParentNode().getId()==null)
      region.setParentNode(null);
    return regionRepository.save(region);
  }

  @Override
  public Region findById(long id) {
    return regionRepository.findOne(id);
  }

  @Override
  public Region update(Region region) {
    Assert.notNull(region, "region must not be null");
    if(region.getParentNode().getId()==null)
      region.setParentNode(null);
    return regionRepository.save(region);
  }


  @Override
  public Map<String, TreeNode> groupCameraByRegion() {
    Map<String, TreeNode> res = new HashMap<>();
    List<Camera> cameras = cameraService.findAll();
    for(Camera camera : cameras){
      if(camera.getRegion() != null) {
        Region region = camera.getRegion();
        TreeNode regionNode = res.get(String.valueOf(region.getId()));
        if (regionNode == null) {
          regionNode = new TreeNode(region.getFullName(), TreeNode.NodeType.folder, -1L, -1L);
          res.put(String.valueOf(region.getId()), regionNode);
        }
        TreeNode cameraNode = new TreeNode(camera.getDeviceName(), TreeNode.NodeType.item, camera.getId(), camera.getAnalyser() != null ? camera.getAnalyser().getId() : -1);

        regionNode.putChildren(String.valueOf(camera.getId()), cameraNode);
      }
    }
    
    return res;
  }

  @Override public List<Region> findNotChildren(Long regionid) {
                Set<Long> regionIds = new HashSet<Long>();
                Region region = regionRepository.findOne(regionid);
                regionIds = getRegionList(regionIds,region);
                List<Region> regions = regionRepository.findByIdNotIn(regionIds);
                return regions;
  }

  @Override public Set<Region> findChildren(Region region) {
           return regionRepository.findByParentNodeIn(region);
  }

  public Set<Long> getRegionList(Set<Long> set,Region region){
          Set<Region> regions = regionRepository.findByParentNodeIn(region);
          set.add(region.getId());
          for(Region region2 : regions){
                 getRegionList(set,region2);
          }
      return  set;
    }

  public static class TreeNode {
    private String text;
    private NodeType type;
    private Long nodeId;
    private Long analyseid ;

    public enum NodeType{
      folder, item
    }
    private Map<String, TreeNode> children = new HashMap<>();
    
    public TreeNode(String text, NodeType type,Long nodeId,Long analyseid) {
      this.text = text;
      this.type = type;
      this.nodeId = nodeId;
      this.analyseid = analyseid;
    }
    public String getText() {
      return text;
    }
    public void setText(String text) {
      this.text = text;
    }
    public NodeType getType() {
      return type;
    }
    public void setType(NodeType type) {
      this.type = type;
    }
    public Map<String, TreeNode> getChildren() {
      return children;
    }
    public void setChildren(Map<String, TreeNode> children) {
      this.children = children;
    }
    
    public void putChildren(String key, TreeNode node){
      children.put(key, node);
    }

    public Long getNodeId() {
      return nodeId;
    }

    public void setNodeId(Long nodeId) {
      this.nodeId = nodeId;
    }

    public Long getAnalyseid() {
      return analyseid;
    }

    public void setAnalyseid(Long analyseid) {
      this.analyseid = analyseid;
    }
  }

  @Override
  public void delete(long id) {

    regionRepository.delete(id);
  }
}
