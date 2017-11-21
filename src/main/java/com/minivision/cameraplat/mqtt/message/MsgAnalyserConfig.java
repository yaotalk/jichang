package com.minivision.cameraplat.mqtt.message;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.minivision.cameraplat.domain.Analyser;
import com.minivision.cameraplat.domain.Camera;

public class MsgAnalyserConfig implements MqttMessageView{
  @JsonView(MqttMessageView.class)
  private Analyser analyser;
  @JsonView(MqttMessageView.class)
  private Set<Camera> cameras;
  
  public MsgAnalyserConfig(Analyser analyser, Set<Camera> cameras) {
    this.analyser = analyser;
    this.cameras = cameras;
  }
  
  public MsgAnalyserConfig(Camera camera) {
    this.cameras = new HashSet<>();
    this.cameras.add(camera);
  }
  public Analyser getAnalyser() {
    return analyser;
  }
  public void setAnalyser(Analyser analyser) {
    this.analyser = analyser;
  }
  public Set<Camera> getCameras() {
    return cameras;
  }
  public void setCameras(Set<Camera> cameras) {
    this.cameras = cameras;
  }
  
  public class MsgAnalyserInfo{
    
  }
  
  public class MsgCameraInfo{
    private String camtype;
    private String ip;
    private String username;
    private String password;
    private String port;
    private String rtsPort;
    
    
    
    public String getCamtype() {
      return camtype;
    }
    public void setCamtype(String camtype) {
      this.camtype = camtype;
    }
    public String getIp() {
      return ip;
    }
    public void setIp(String ip) {
      this.ip = ip;
    }
    public String getUsername() {
      return username;
    }
    public void setUsername(String username) {
      this.username = username;
    }
    public String getPassword() {
      return password;
    }
    public void setPassword(String password) {
      this.password = password;
    }
    public String getPort() {
      return port;
    }
    public void setPort(String port) {
      this.port = port;
    }
    public String getRtsPort() {
      return rtsPort;
    }
    public void setRtsPort(String rtsPort) {
      this.rtsPort = rtsPort;
    }
  }
}
