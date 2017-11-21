package com.minivision.cameraplat.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

@Entity
public class AnalyserStatus extends IdEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "analyser_id", foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
  @JsonUnwrapped(prefix="analyser_")
  private Analyser analyser;
  private float cpu;
  private float mem;
  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp;
  @Transient
  private List<CameraStatus> devStatus;
  
  public static class CameraStatus{
    private long id;
    private int status;
    public long getId() {
      return id;
    }
    public void setId(long id) {
      this.id = id;
    }
    public int getStatus() {
      return status;
    }
    public void setStatus(int status) {
      this.status = status;
    }
  }

  public List<CameraStatus> getDevStatus() {
    return devStatus;
  }

  public void setDevStatus(List<CameraStatus> devStatus) {
    this.devStatus = devStatus;
  }

  public Analyser getAnalyser() {
    return analyser;
  }

  public void setAnalyser(Analyser analyser) {
    this.analyser = analyser;
  }

  public float getCpu() {
    return cpu;
  }

  public void setCpu(float cpu) {
    this.cpu = cpu;
  }

  public float getMem() {
    return mem;
  }

  public void setMem(float mem) {
    this.mem = mem;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "AnalyserStatus [analyser=" + analyser.getId() + ", cpu=" + cpu + ", mem="
        + mem + ", timestamp=" + timestamp + "]";
  }
  
  
}
