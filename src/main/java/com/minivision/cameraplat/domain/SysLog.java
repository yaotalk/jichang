package com.minivision.cameraplat.domain;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
@Entity
public class SysLog extends IdEntity {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userid",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
  @NotFound(action = NotFoundAction.IGNORE)
  private User user;
  private String ip;
  private String modelName; // 操作模块
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTime;
  private String opration;
  @Column(length = 500)
  private String oprateDetails;

  public SysLog(User user, String ip, String modelName, String opration, Date createTime,String oprateDetails) {
    this.user = user;
    this.ip = ip;
    this.modelName = modelName;
    this.opration = opration;
    this.createTime = createTime;
    this.oprateDetails = oprateDetails;
  }

  public String getOprateDetails() {
    return oprateDetails;
  }

  public void setOprateDetails(String oprateDetails) {
    this.oprateDetails = oprateDetails;
  }

  public SysLog() {}

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getCreateTime() throws Exception {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time = sf.format(createTime);
    return time;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public String getOpration() {
    return opration;
  }

  public void setOpration(String opration) {
    this.opration = opration;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

}
