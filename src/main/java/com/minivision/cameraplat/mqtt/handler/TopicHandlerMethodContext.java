package com.minivision.cameraplat.mqtt.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.cameraplat.domain.MonitorImage;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.MqttMessageJson;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.MqttMessageParam;

import io.moquette.interception.messages.InterceptPublishMessage;

public class TopicHandlerMethodContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(TopicHandlerMethodContext.class);
  
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  
  private Object instance;
  private Method method;
  
  public TopicHandlerMethodContext(Object instance, Method method) {
    this.instance = instance;
    this.method = method;
  }
  
  
  public Object process(InterceptPublishMessage message) {
    try {
      Parameter[] ps = method.getParameters();
      List<Object> args = new ArrayList<>();
      for(Parameter p : ps){
        Object arg = searchArg(p, message);
        args.add(arg);
      }
      
      Object object = method.invoke(instance, args.toArray());
      LOGGER.trace("invoke message process method , service:{}, name:{}, data:{}", instance.getClass().getName(), method.getClass().getName(), args);
      return object;
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      LOGGER.error("invoke message process method error ", e);
    }
    return null;
  }
  
  
  private Object searchArg(Parameter p , InterceptPublishMessage message){
    if(p.getType().equals(ByteBuffer.class)){
      return message.getPayload();
    }
    if(p.getType().equals(MonitorImage.class)){
      return decode(message.getPayload());
    }
    if(p.isAnnotationPresent(MqttMessageJson.class)){
      try {
        return OBJECT_MAPPER.readValue(message.getPayload().array(), p.getType());
      } catch (IOException e) {
        throw new RuntimeException("json deserialize error", e);
      }
    }
    if(p.getType().equals(String.class)){
      MqttMessageParam annotation = p.getDeclaredAnnotation(MqttMessageParam.class);
      switch(annotation.value()){
        case clientId:
          return message.getClientID(); 
        case username:
          return message.getUsername();
      }
    }
    return null;
  }
  
  
  private static final int IMAGE_HEADER_LENGTH = 128;
  private MonitorImage decode(ByteBuffer payload){
    long serialNo = payload.getLong();
    long timestamp = payload.getLong();
    int cameraId = payload.getInt();
    int size = payload.getInt();
    int trackId = payload.getInt();
    payload.position(IMAGE_HEADER_LENGTH);
    int remaining = payload.remaining();
    Assert.isTrue(remaining == size,"bad packet");
    byte[] image = new byte[size];
    payload.get(image);
    return new MonitorImage(serialNo, timestamp, cameraId, trackId, image);
  }
  
}
