package com.minivision.cameraplat.rest;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.minivision.cameraplat.domain.Face;
import com.minivision.cameraplat.domain.FaceSet;
import com.minivision.cameraplat.mvc.ex.ServiceException;
import com.minivision.cameraplat.rest.param.alarm.SnapSearchParam;
import com.minivision.cameraplat.rest.param.faceset.FaceSearchParam;
import com.minivision.cameraplat.rest.param.faceset.FacesetParam;
import com.minivision.cameraplat.rest.result.PageResult;
import com.minivision.cameraplat.rest.result.RestResult;
import com.minivision.cameraplat.rest.result.alarm.SnapSearchResult;
import com.minivision.cameraplat.rest.result.faceset.FaceSearchResult;
import com.minivision.cameraplat.service.FaceService;
import com.minivision.cameraplat.service.FaceSetService;

import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/v1")
@Api(tags = "FacesetApi", value = "faceset Apis")
public class FacesetApi {

  //private static final Logger LOGGER = LoggerFactory.getLogger(FacesetApi.class);

  @Autowired
  private FaceSetService faceSetService;

  @Autowired
  private FaceService faceService;

  @GetMapping(value = "facesets")
  @ApiOperation(value = "人脸库查询", notes = "人脸库查询")
  public RestResult<List<FaceSet>> getFacesets() {
    List<FaceSet> list = faceSetService.findAll();
    return new RestResult<>(list);
  }

  @GetMapping(value = "faces")
  @ApiOperation(value = "人脸库人脸查询", notes = "人脸库人脸查询")
  public RestResult<PageResult<Face>> getFacesets(@ModelAttribute FacesetParam facesetParam) {

    Page<Face> page = faceService.findByFacesetId(facesetParam);
    return new RestResult<>(new PageResult<>(page));
  }

  @PostMapping(value = "faceSearch")
  @ApiOperation(value = "人脸检索", notes = "对输入的人脸照片，和选择的人脸库进行比对，输出前N条相似的人")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "imgfile", paramType = "form", dataType = "file", required = true)})
  public RestResult<List<FaceSearchResult>> faceSearch(@ModelAttribute FaceSearchParam param) {
    List<FaceSearchResult> faceSearchResults = null;
    try {
         faceSearchResults = faceService.searchByPlat(param);
    } catch (ServiceException e) {
        return new RestResult<>(e.getMessage());
    }catch (Exception e){
        return new RestResult<>(e);
    }
    return new RestResult<>(faceSearchResults);
  }

  @GetMapping("snapSearch")
  @ApiOperation(value = "抓拍检索", notes = "对输入的人脸照片，和相应通道的所以时间段内的抓拍照片进行比对，获取比对结果。(暂无)")
  @ApiImplicitParams({@ApiImplicitParam(name = "imgfile", paramType = "form", dataType = "file")})
  public RestResult<List<SnapSearchResult>> snapSearch(
      @ModelAttribute @Valid SnapSearchParam param) {
    return new RestResult<>();
  }


}
