package com.minivision.cameraplat.domain;

public class MonitorImage {
  private long serialNo;
  private long timestamp;
  private int cameraId;
  private int trackId;
  private byte[] image;
  
  private String fileName;
  private String fileUrl;
  
  public MonitorImage(long serialNo, long timestamp, int cameraId, int trackId,  byte[] image) {
    this.serialNo = serialNo;
    this.timestamp = timestamp;
    this.cameraId = cameraId;
    this.trackId = trackId;
    this.image = image;
  }
  public long getSerialNo() {
    return serialNo;
  }
  public void setSerialNo(long serialNo) {
    this.serialNo = serialNo;
  }
  public long getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  public int getCameraId() {
    return cameraId;
  }
  public void setCameraId(int cameraId) {
    this.cameraId = cameraId;
  }
  public int getTrackId() {
    return trackId;
  }
  public void setTrackId(int trackId) {
    this.trackId = trackId;
  }
  public byte[] getImage() {
    return image;
  }
  public void setImage(byte[] image) {
    this.image = image;
  }
  public String getFileName() {
    return fileName;
  }
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public String getFileUrl() {
    return fileUrl;
  }
  public void setFileUrl(String fileUrl) {
    this.fileUrl = fileUrl;
  }
  @Override
  public String toString() {
    return "MonitorImage [serialNo=" + serialNo + ", timestamp=" + timestamp + ", cameraId="
        + cameraId + ", trackId=" + trackId + ", image_size=" + image.length + "]";
  }
  
  
  
}
