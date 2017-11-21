package com.minivision.cameraplat.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;
import com.minivision.cameraplat.mqtt.message.MqttMessageView;

@MappedSuperclass
public abstract class IdEntity implements Serializable{
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @JsonView(MqttMessageView.class)
  protected Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
