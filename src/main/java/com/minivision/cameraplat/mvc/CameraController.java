package com.minivision.cameraplat.mvc;

import com.minivision.cameraplat.config.OpAnnotation;
import com.minivision.cameraplat.domain.*;
import com.minivision.cameraplat.mvc.ex.ServiceException;
import com.minivision.cameraplat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
@RequestMapping("camera")
public class CameraController {

  @Autowired
  private CameraService cameraService;

  @Autowired
  private FaceSetService faceSetService;

  @Autowired
  private RegionService regionService;

  @Autowired
  private AnalyserService analyserService;

  @Autowired
  private StrategyService strategyService;

  @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
  public ModelAndView pageinfo() {

    List<FaceSet> faceSets = faceSetService.findAll();
    List<Region> regions = regionService.findAll();
    Iterable<Analyser> analysers = analyserService.findAll();
    List<Strategy> strategies = strategyService.findAll();
    return new ModelAndView("sysmanage/cameralist").addObject("analysers", analysers)
        .addObject("facesets", faceSets).addObject("strategies", strategies)
        .addObject("regions", regions);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Iterable<CameraServiceImpl.CameraShow> cameras() {
    Iterable<CameraServiceImpl.CameraShow> cameras = this.cameraService.findAllWithStatus();
    return cameras;
  }

  @PostMapping
  @OpAnnotation(modelName = "摄像机", opration = "新增摄像机")
  public String createCamera(Camera camera) {
    try {
      this.cameraService.create(camera);
    } catch (ServiceException e) {
      e.getMessage();
      return "failed";
    }
    return "success";
  }

  @PatchMapping
  @OpAnnotation(modelName = "摄像机", opration = "编辑摄像机")
  public String updateCamera(Camera camera) {
    try {
      this.cameraService.update(camera);
    } catch (ServiceException e) {
      return "failed";
    }
    return "success";
  }


  @DeleteMapping
  @OpAnnotation(modelName = "摄像机", opration = "删除摄像机")
  public String deleteCamera(Camera camera) {
    this.cameraService.delete(camera);
    return "success";
  }


  @GetMapping("/getCamara")
  public Camera getCamara(@RequestParam(value = "id", defaultValue = "") String id) {
    return this.cameraService.findByid(Long.valueOf(id));
  }

  @GetMapping("/bindcameras")
  public ModelAndView tree() {
    return new ModelAndView("sysmanage/bindcameras");
  }

  @GetMapping("/gerMenus")
  public Map<String, RegionServiceImpl.TreeNode> getForMenu() {
    Map<String, RegionServiceImpl.TreeNode> map = regionService.groupCameraByRegion();
    return map;
  }

  @GetMapping("/updataAnaCamera")
  @OpAnnotation(modelName = "摄像机", opration = "关联摄像机")
  public String updateAnaCamera(
      @RequestParam(value = "analyserid", defaultValue = "") Long analyserid,
      @RequestParam(value = "items", defaultValue = "") List<String> items) {
    Analyser analyser = this.analyserService.findById(analyserid);
    Set<Camera> anaCameras = analyser.getCameras();

    List<Long> ids = new ArrayList<Long>();
    for (String id : items) {
      ids.add(Long.valueOf(id));
    }
    Set<Camera> cameras = this.cameraService.findByIds(ids);
    for (Camera camera : anaCameras) {
      camera.setAnalyser(null);
    }
    for (Camera camera : cameras) {
      camera.setAnalyser(analyser);
    }
    analyser.setCameras(cameras);
    this.analyserService.update(analyser);
    return "success";
  }
}
