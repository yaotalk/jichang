package com.minivision.cameraplat.domain.record;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.minivision.cameraplat.domain.Face;

@Document
public class MonitorRecord extends IdDocument{
  private SnapshotRecord snapshot;
  @Indexed
  private FaceInfo face;
  
  public MonitorRecord() {
    
  }

  public MonitorRecord(SnapshotRecord snapshot) {
    this.snapshot = snapshot;
  }
  
  public SnapshotRecord getSnapshot() {
    return snapshot;
  }
  public void setSnapshot(SnapshotRecord snapshot) {
    this.snapshot = snapshot;
  }
  
  public FaceInfo getFace() {
    return face;
  }
  
  public void setFace(FaceInfo face) {
    this.face = face;
  }
  
  

  public static class FaceInfo{
    private String id;
    private String name;
    private String sex;
    private String faceSet;
    private String imgUrl;

    public FaceInfo(){};
    
    public FaceInfo(Face face){
      this.id = face.getId();
      this.name = face.getName();
      this.sex = face.getSex();
      this.faceSet = face.getFaceSet().getToken();
      this.imgUrl = face.getImgUrl();
    }

    public String getImgUrl() {
      return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
      this.imgUrl = imgUrl;
    }

    public String getId() {
      return id;
    }
    public void setId(String id) {
      this.id = id;
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
    public String getFaceSet() {
      return faceSet;
    }
    public void setFaceSet(String faceSet) {
      this.faceSet = faceSet;
    }
    
  }

}
