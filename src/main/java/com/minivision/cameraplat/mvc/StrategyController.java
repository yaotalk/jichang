package com.minivision.cameraplat.mvc;

import com.minivision.cameraplat.config.OpAnnotation;
import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Scheme;
import com.minivision.cameraplat.domain.Strategy;
import com.minivision.cameraplat.service.CameraService;
import com.minivision.cameraplat.service.SchemeService;
import com.minivision.cameraplat.service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/strategy")
public class StrategyController {

  @Autowired
  private StrategyService strategyService;

  @Autowired
  private SchemeService schemeService;

  @Autowired
  private CameraService cameraService;

  @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
  public ModelAndView page() {
    List<Scheme> schemes = schemeService.findAll();
    return new ModelAndView("strategy/strategylist").addObject("schemes",schemes);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<Strategy> list() {
    return strategyService.findAll();
  }

  @PostMapping
  @OpAnnotation(modelName = "布控策略",opration = "新增布控策略")
  public String addStrategy(Strategy strategy, String schemeId) {
    if (!StringUtils.isEmpty(schemeId)) {
      Scheme scheme = schemeService.findOne(Long.valueOf(schemeId));
      strategy.setScheme(scheme);
    }
    strategyService.create(strategy);
    return "success";
  }

  @PatchMapping
  @OpAnnotation(modelName = "布控策略",opration = "修改布控策略")
  public String updateStrategy(Strategy strategy, String schemeId) {
    if (!StringUtils.isEmpty(schemeId)) {
      Scheme scheme = schemeService.findOne(Long.valueOf(schemeId));
      strategy.setScheme(scheme);
    }
    strategyService.update(strategy);
    return "success";
  }

  @GetMapping("isused")
  public String isused(String id) {
    List<Camera> cameras = cameraService.findByStategy(Long.valueOf(id));
    if (cameras.size() > 0) {
      return "当前有" + cameras.size() + "个摄像头正在使用该策略，是否继续删除？";
    }
    return "success";
  }

  @DeleteMapping
  @OpAnnotation(modelName = "布控策略",opration = "删除布控策略")
  public String deleteStrategy(Strategy strategy) {
    strategyService.delete(strategy);
    return "success";
  }
}
