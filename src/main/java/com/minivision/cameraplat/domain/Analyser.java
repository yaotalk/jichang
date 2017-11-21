package com.minivision.cameraplat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class Analyser extends IdEntity{

  @Column(name = "analyser_name")
  @NotEmpty(message = "name is required.")
  private String name;

  @NotEmpty(message = "ip is required.")
  private String ip;
  
  @NotEmpty(message = "port is required")
  private String port;

  private String username;
  private String password;

  @JsonIgnore
  @OneToMany(fetch=FetchType.EAGER, mappedBy="analyser")
  private Set<Camera> cameras;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
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

  public Set<Camera> getCameras() {
    return cameras;
  }

  public void setCameras(Set<Camera> cameras) {
    this.cameras = cameras;
  }

  @Override public String toString() {
    return "分析仪{id = " + id +'\'' + ",名称='" + name + '\'' + ", ip='" + ip + '\'' + ", 端口='" + port + '\''
        + ", 用户名='" + username + '\'' + ", 密码='" + password + '\'' + "} ";
  }

}
