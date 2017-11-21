package com.minivision.cameraplat.mvc;

import com.minivision.cameraplat.config.OpAnnotation;
import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Region;
import com.minivision.cameraplat.service.CameraService;
import com.minivision.cameraplat.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("region")
public class RegionController {

  @Autowired
  private CameraService cameraService;

  @Autowired
  private RegionService regionService;

  @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
  public ModelAndView page() {
    return new ModelAndView("sysmanage/regionlist");
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<Region> list(@RequestParam(value = "regionid", defaultValue = "") String regionid) {
    List<Region> regions = null;
    if (!StringUtils.isEmpty(regionid)) {
      regions = regionService.findNotChildren(Long.valueOf(regionid));
    } else {
      regions = this.regionService.findAll();
    }
    return regions;
  }

  @PostMapping
  @OpAnnotation(modelName = "区域",opration = "新增区域")
  public String addRegion(Region region) {
    this.regionService.create(region);
    return "success";
  }

  @PatchMapping
  @OpAnnotation(modelName = "区域",opration = "编辑区域")
  public String updateRegion(Region region) {
    this.regionService.update(region);
    return "success";
  }

  @DeleteMapping
  @OpAnnotation(modelName = "区域",opration = "删除区域")
  public String deleteRegion(Region region) {
    Region old_region = regionService.findById(region.getId());
    // 判断是否含有子节点
    if (regionService.findChildren(old_region).size() > 0) {
      return "删除失败，该区域为父区域，无法删除";
    }
    // 判断是否已经关联摄像头
    List<Camera> cameras = cameraService.findByRegion(old_region);
    if (cameras.size() > 0) {
      return "删除失败，该区域包含摄像头，请先删除摄像头";
    }
    regionService.delete(old_region.getId());
    return "success";
  }
}
