package com.minivision.cameraplat.rest.param.alarm;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import com.minivision.cameraplat.rest.param.RestParam;

public class SnapSearchParam extends RestParam {
  private static final long serialVersionUID = -534755979304192089L;
  private MultipartFile img;
  private long startTime;
  private long endTime;
  private long cameraId;
  private float threshold;
  @ApiModelProperty(value = "记录总数")
  private int limit =10;

  public float getThreshold() {
    return threshold;
  }

  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }

  public MultipartFile getImg() {
    return img;
  }

  public void setImg(MultipartFile img) {
    this.img = img;
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

  public long getCameraId() {
    return cameraId;
  }

  public void setCameraId(long cameraId) {
    this.cameraId = cameraId;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

}
