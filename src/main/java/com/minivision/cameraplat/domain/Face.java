package com.minivision.cameraplat.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Face {
  @Id
  @NotEmpty(message = "Id is required.")
  private String id;
  private String name;
  private String sex;
  private String idCard;
  private String employeeId;
  private Integer picSize;
  private String picMd5;
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTime;
  
  private String phoneNumber;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "faceset_id",
      foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
  private FaceSet faceSet;
  private String imgpath;
  private String imgUrl;

  public Face() {}

  public Face(String name, String sex, String idCard, String phoneNumber, FaceSet faceSet,
      String imgpath) {
    this.name = name;
    this.sex = sex;
    this.idCard = idCard;
    this.phoneNumber = phoneNumber;
    this.faceSet = faceSet;
    this.imgpath = imgpath;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getImgpath() {
    return imgpath;
  }

  public void setImgpath(String imgpath) {
    this.imgpath = imgpath;
  }

  public String getImgUrl() {
	return imgUrl;
}

public void setImgUrl(String imgUrl) {
	this.imgUrl = imgUrl;
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

  public String getIdCard() {
    return idCard;
  }

  public void setIdCard(String idCard) {
    this.idCard = idCard;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public FaceSet getFaceSet() {
    return faceSet;
  }

  public void setFaceSet(FaceSet faceSet) {
    this.faceSet = faceSet;
  }

  public Integer getPicSize() {
    return picSize;
  }

  public void setPicSize(Integer picSize) {
    this.picSize = picSize;
  }

  public String getPicMd5() {
    return picMd5;
  }

  public void setPicMd5(String picMd5) {
    this.picMd5 = picMd5;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  @Override
  public String toString() {
    return "人脸{" + "id='" + id + '\'' + ", 姓名='" + name + '\'' + ", 性别（0男1女）='" + sex + '\''
        + ", 身份证号='" + idCard + '\'' + ", 工号='" + employeeId + '\'' + ", 电话号码='" + phoneNumber
        + '\'' + ", 所属人脸库token=" + faceSet.getToken() + ", 图片路径='" + imgpath + '\'' + ", 文件大小='"
        + picSize + '\'' + ", 文件的md5值='" + picMd5 + '\'' + '}';
  }

}
