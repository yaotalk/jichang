package com.minivision.cameraplat.domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import com.minivision.cameraplat.mqtt.message.MsgCameraStrategy;

@Entity
public class Strategy extends IdEntity {
  private String name;
  private StrategyType type;
  private boolean snapshot;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "scheme_id")
  @JsonView(MsgCameraStrategy.class)
  @JsonUnwrapped(prefix="scheme_")
  private Scheme scheme;
  private int maxfaceNum;
  private double compareThreshold;
  @JsonView(MsgCameraStrategy.class)
  private int minFaceSize;
  private int preserveDays;
  @JsonView(MsgCameraStrategy.class)
  private int faceQualityThreshold;
  
  private int intervals;
  @JsonView(MsgCameraStrategy.class)
  private int snapInterval;
  @JsonView(MsgCameraStrategy.class)
  private int retryCounts;
  @JsonView(MsgCameraStrategy.class)
  private boolean forbidMultiFace;
  @JsonView(MsgCameraStrategy.class)
  private int forbidMultiFaceInterval;
  
  public enum StrategyType{
    BlackList, WhiteList, DynaminFaceRepo
  }

  public int getForbidMultiFaceInterval() {
    return forbidMultiFaceInterval;
  }

  public void setForbidMultiFaceInterval(int forbidMultiFaceInterval) {
    this.forbidMultiFaceInterval = forbidMultiFaceInterval;
  }

  public int getSnapInterval() {
    return snapInterval;
  }

  public void setSnapInterval(int snapInterval) {
    this.snapInterval = snapInterval;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public StrategyType getType() {
    return type;
  }

  public void setType(StrategyType type) {
    this.type = type;
  }

  public boolean isSnapshot() {
    return snapshot;
  }

  public void setSnapshot(boolean snapshot) {
    this.snapshot = snapshot;
  }

  public Scheme getScheme() {
    return scheme;
  }

  public void setScheme(Scheme scheme) {
    this.scheme = scheme;
  }

  public int getMaxfaceNum() {
    return maxfaceNum;
  }

  public void setMaxfaceNum(int maxfaceNum) {
    this.maxfaceNum = maxfaceNum;
  }

  public double getCompareThreshold() {
    return compareThreshold;
  }

  public void setCompareThreshold(double compareThreshold) {
    this.compareThreshold = compareThreshold;
  }

  public int getMinFaceSize() {
    return minFaceSize;
  }

  public void setMinFaceSize(int minFaceSize) {
    this.minFaceSize = minFaceSize;
  }

  public int getPreserveDays() {
    return preserveDays;
  }

  public void setPreserveDays(int preserveDays) {
    this.preserveDays = preserveDays;
  }

  public int getFaceQualityThreshold() {
    return faceQualityThreshold;
  }

  public void setFaceQualityThreshold(int faceQualityThreshold) {
    this.faceQualityThreshold = faceQualityThreshold;
  }

  public int getIntervals() {
    return intervals;
  }

  public void setIntervals(int intervals) {
    this.intervals = intervals;
  }

  public int getRetryCounts() {
    return retryCounts;
  }

  public void setRetryCounts(int retryCounts) {
    this.retryCounts = retryCounts;
  }
  
  public boolean isForbidMultiFace() {
    return forbidMultiFace;
  }

  public void setForbidMultiFace(boolean forbidMultiFace) {
    this.forbidMultiFace = forbidMultiFace;
  }

  @Override public String toString() {
    return "布控策略{id="  + id + '\'' + ",名称='" + name + '\'' + ", 类型=" + type + ", 是否抓拍="
        + snapshot + ", 时间方案=" + (scheme==null?null:scheme.getId()) + ", 最大人脸数=" + maxfaceNum + ", 比对阈值="
        + compareThreshold + ", 最小人脸像素=" + minFaceSize + ", 保存天数=" + preserveDays
        + ", 人脸质量阈值=" + faceQualityThreshold + ", 识别入库间隔=" + intervals
        + ", 抓拍间隔=" + snapInterval + ", 重试次数=" + retryCounts + '}';
  }
}
