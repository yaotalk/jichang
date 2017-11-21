package com.minivision.cameraplat.domain.record;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;
import com.minivision.cameraplat.mqtt.message.MqttMessageView;

@MappedSuperclass
public class IdDocument {
  @Id
  @JsonView(MqttMessageView.class)
  protected String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
}
