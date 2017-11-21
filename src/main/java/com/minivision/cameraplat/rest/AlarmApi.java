package com.minivision.cameraplat.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.minivision.cameraplat.domain.record.MonitorRecord;
import com.minivision.cameraplat.domain.record.SnapshotRecord;
import com.minivision.cameraplat.rest.param.alarm.AlarmFaceParam;
import com.minivision.cameraplat.rest.param.alarm.AlarmParam;
import com.minivision.cameraplat.rest.param.alarm.SnapShotParam;
import com.minivision.cameraplat.rest.result.PageResult;
import com.minivision.cameraplat.rest.result.RestResult;
import com.minivision.cameraplat.rest.result.alarm.AlarmFacesResult;
import com.minivision.cameraplat.service.FaceService;
import com.minivision.cameraplat.service.MonitorRecordService;
import com.minivision.cameraplat.service.SnapShotRecordService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1")
@Api(tags = "AlarmApi", value = "Alarm Apis")
public class AlarmApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlarmApi.class);

  @Autowired
  private SnapShotRecordService snapShotRecordService;

  @Autowired
  MonitorRecordService monitorRecordService;

  @Autowired
  FaceService faceService;

  @GetMapping(value = "alarm")
  @ApiOperation(value = "报警查询", notes = "查询指定时间平台中某个设备的报警日志信息")
  public RestResult<PageResult<MonitorRecord>> getAlarmLog(
      @Valid @ModelAttribute AlarmParam param) {
    LOGGER.trace("alarm search, param: {}", ToStringBuilder.reflectionToString(param, ToStringStyle.SHORT_PREFIX_STYLE));
    PageResult<MonitorRecord> result = monitorRecordService.findMonitorRecords(param);
    LOGGER.trace("alarm search, result: {}", ToStringBuilder.reflectionToString(result, ToStringStyle.SHORT_PREFIX_STYLE));
    return new RestResult<>(result);
  }

  @GetMapping(value = "alarmFaces")
  @ApiOperation(value = "报警照片", notes = "通过报警ID获取该报警对应的最接近的前N人")
  public RestResult<List<AlarmFacesResult>> getAlarmFace(@Valid @ModelAttribute AlarmFaceParam param) {
    LOGGER.trace("emergencyFaces search at api");
    List<AlarmFacesResult> list = faceService.searchByFlatForAlarm(param);
    LOGGER.trace("emergencyFaces search at api");
    return new RestResult<>(list);
  }

  @GetMapping(value = "snapShot")
  @ApiOperation(value = "抓拍查询", notes = "获取平台固定时间段的抓拍信息")
  public RestResult<PageResult<SnapshotRecord>> getsnapShots(@Valid @ModelAttribute SnapShotParam param) {
    LOGGER.trace("snapShot search, param: {}", ToStringBuilder.reflectionToString(param, ToStringStyle.SHORT_PREFIX_STYLE));
    PageResult<SnapshotRecord> page = snapShotRecordService.findByTimeandCameraId(param);
    LOGGER.trace("snapShot search, result: {}", ToStringBuilder.reflectionToString(page, ToStringStyle.SHORT_PREFIX_STYLE));
    return new RestResult<>(page);
  }

}
