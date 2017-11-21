package com.minivision.cameraplat.mqtt.service;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.cameraplat.mqtt.MessageContext;
import com.minivision.cameraplat.mqtt.RequestFuture;
import com.minivision.cameraplat.mqtt.ex.MqttOpException;
import com.minivision.cameraplat.mqtt.message.Packet;

import io.moquette.parser.proto.messages.PublishMessage;
import io.moquette.parser.proto.messages.AbstractMessage.QOSType;
import io.moquette.server.Server;

@Component
public class PublishMessageTemplate {
  
  @Autowired
  private MessageContext msgContext;
  
  @Autowired
  private Server mqttBroker;
  
  @Autowired
  private ObjectMapper oMapper;
  
  public void sendTo(String topic, Packet<?> packet){
    sendTo(topic, packet, true);
  }
  
  public void sendTo(String topic, Packet<?> packet, boolean viewFilter){
    PublishMessage pm = new PublishMessage();
    pm.setTopicName(topic);
    byte[] bytes;
    try {
      if(viewFilter){
        bytes = oMapper.writerWithView(getFilterView(packet)).writeValueAsBytes(packet);
      }else{
        bytes = oMapper.writeValueAsBytes(packet);
      }
      pm.setPayload(ByteBuffer.wrap(bytes));
      pm.setQos(QOSType.MOST_ONE);
      mqttBroker.internalPublish(pm);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
  
  private Class<?> getFilterView(Packet<?> packet){
    if(packet.getBody() == null){
      return null;
    }
    if(packet.getBody() instanceof Collection){
      Collection<?> collection = ((Collection<?>) packet.getBody());
      if(collection.isEmpty()){
        return null;
      }
      return collection.iterator().next().getClass();
    }
    
    return packet.getBody().getClass();
  }
  
  public <T> RequestFuture<T> sendRequest(String topic, Packet<?> request, Class<T> responseBodyType){
    return sendRequest(topic, request, responseBodyType, true);
  }
  
  public <T> RequestFuture<T> sendRequest(String topic, Packet<?> request, Class<T> responseBodyType, boolean viewFilter){
    RequestFuture<T> future = new RequestFuture<>(request, responseBodyType);
    PublishMessage pm = new PublishMessage();
    pm.setTopicName(topic);
    byte[] bytes;
    try {
      if(viewFilter){
        bytes = oMapper.writerWithView(getFilterView(request)).writeValueAsBytes(request);
      }else{
        bytes = oMapper.writeValueAsBytes(request);
      }
      pm.setPayload(ByteBuffer.wrap(bytes));
      pm.setQos(QOSType.MOST_ONE);
      mqttBroker.internalPublish(pm);
      msgContext.add(future);
      return future;
    } catch (JsonProcessingException e) {
      throw new MqttOpException("request send out fail", e);
    }
  }
  
}
