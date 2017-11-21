package com.minivision.cameraplat.mvc;

import com.minivision.cameraplat.config.OpAnnotation;
import com.minivision.cameraplat.domain.Analyser;
import com.minivision.cameraplat.service.AnalyserService;
import com.minivision.cameraplat.service.AnalyserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("analyser")
public class AnalyserController {

  @Autowired
  private AnalyserService analyserService;

  @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
  public ModelAndView analyerpage() {
    return new ModelAndView("sysmanage/analyserlist");
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<AnalyserServiceImpl.AnalyserShow> list() {
    List<AnalyserServiceImpl.AnalyserShow> analyser = analyserService.findAllWithStatus();
    return analyser;
  }

  @PostMapping
  @OpAnnotation(modelName = "分析仪",opration = "新增分析仪")
  public String createAnalyser(Analyser analyser) {
    this.analyserService.create(analyser);
    return "success";
  }

  @DeleteMapping
  @OpAnnotation(modelName = "分析仪",opration = "删除分析仪")
  public String deleteAnalyser(Analyser analyser) {
    Analyser oldanalyser = analyserService.findById(analyser.getId());
    if (oldanalyser.getCameras().size() > 0) {
      return "删除失败，该人脸分析仪已关联摄像头";
    }
    this.analyserService.delete(analyser);
    return "success";
  }

  @PatchMapping
  @OpAnnotation(modelName = "分析仪",opration = "编辑分析仪")
  public String updateAnalyser(Analyser analyser) {
    this.analyserService.update(analyser);
    return "success";
  }
}
