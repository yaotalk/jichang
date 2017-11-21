package com.minivision.cameraplat.rest.param.alarm;

import com.minivision.cameraplat.rest.param.RestParam;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class AlarmParam extends RestParam {

  private static final long serialVersionUID = -5324699653300472053L;

  @NotNull(message = "startTime can not be empty")
  private long startTime;

  @NotNull(message = "endTime can not be empty")
  private long endTime;

  @ApiModelProperty(value = "摄像机ID")
  private Long cameraId;

  private String name;

  private String sex;

  private String faceToken;

  private int offset = 0;

  @Max(100)
  private int limit = 10;

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public Long getCameraId() {
    return cameraId;
  }

  public void setCameraId(Long cameraId) {
    this.cameraId = cameraId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getFaceToken() {
    return faceToken;
  }

  public void setFaceToken(String faceToken) {
    this.faceToken = faceToken;
  }

  @Override
  public String toString() {
    return "AlarmParam [startTime=" + startTime + ", endTime=" + endTime + ", cameraId=" + cameraId
        + ", name=" + name + ", sex=" + sex + ", faceToken=" + faceToken + ", offset=" + offset
        + ", limit=" + limit + "]";
  }

}
