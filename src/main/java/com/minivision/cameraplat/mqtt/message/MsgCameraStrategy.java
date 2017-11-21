package com.minivision.cameraplat.mqtt.message;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import com.minivision.cameraplat.domain.Strategy;

public class MsgCameraStrategy implements MqttMessageView{
  @JsonView(MqttMessageView.class)
  @JsonUnwrapped()
  private Strategy strategy;

  public MsgCameraStrategy(Strategy strategy) {
    this.strategy = strategy;
  }

  public Strategy getStrategy() {
    return strategy;
  }

  public void setStrategy(Strategy strategy) {
    this.strategy = strategy;
  }
  
}
