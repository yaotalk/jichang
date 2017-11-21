package com.minivision.cameraplat.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonView;
import com.minivision.cameraplat.mqtt.message.MsgCameraStrategy;

import java.util.List;

@Entity
public class Scheme extends IdEntity{
  public Scheme() {}

  private String name;
  
  @Id
  private Long id;

  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE,CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JsonView(MsgCameraStrategy.class)
  private List<Period> period;
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Period> getPeriod() {
    return period;
  }

  public void setPeriod(List<Period> period) {
    this.period = period;
  }

  @Entity(name = "period")
  public static class Period extends IdEntity {
    @Id
    private Long id;
    
    @JsonView(MsgCameraStrategy.class)
    private String startTime;
    @JsonView(MsgCameraStrategy.class)
    private String endTime;
    @JsonView(MsgCameraStrategy.class)
    public int weekday;

    public Period() {}

    public Period(String startTime, String endTime, int weekday) {
      this.startTime = startTime;
      this.endTime = endTime;
      this.weekday = weekday;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getStartTime() {
      return startTime;
    }

    public String getEndTime() {
      return endTime;
    }

    public void setEndTime(String endTime) {
      this.endTime = endTime;
    }

    public int getWeekday() {
      return weekday;
    }

    public void setWeekday(int weekday) {
      this.weekday = weekday;
    }

    public void setStartTime(String startTime) {
      this.startTime = startTime;
    }

    @Override public String toString() {
      return "周期{" + "id=" + id + ", 开始时间='" + startTime + '\'' + ", 结束时间='" + endTime
          + '\'' + ", weekday=" + weekday + '}';
    }
  }

  @Override public String toString() {
    return "时间方案{id="  + id + '\'' +",名称='" + name + '\'' +  '}';
  }
}
