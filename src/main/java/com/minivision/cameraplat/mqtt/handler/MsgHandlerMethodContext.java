package com.minivision.cameraplat.mqtt.handler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.MqttMessageBody;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.MqttMessageParam;
import com.minivision.cameraplat.mqtt.message.Packet;
import com.minivision.cameraplat.mqtt.message.PacketUtils;
import com.minivision.cameraplat.mqtt.message.Packet.Head;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Type;


public class MsgHandlerMethodContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(MsgHandlerMethodContext.class);
  private Object instance;
  private Method method;
  
  public MsgHandlerMethodContext(Object instance, Method method) {
    this.instance = instance;
    this.method = method;
  }
  
  
  public Packet<?> process(String clientId, String username, Head head, JsonParser parser) throws Exception {
      Parameter[] ps = method.getParameters();
      List<Object> args = new ArrayList<>();
      for(Parameter p : ps){
        Object arg = searchArg(p, clientId, username, head, parser);
        args.add(arg);
      }
      
      Object o = method.invoke(instance, args.toArray());
      
      LOGGER.trace("invoke message process method , service:{}, name:{}, data:{}", instance.getClass().getName(), method.getClass().getName(), args);
      
      if (o == null) {
          if (method.getReturnType() == void.class || method.getReturnType() == Void.class)
              return null;
          o = Type.RESPONSE_OK;// default set return type to ok
      }

      Packet<?> rs;
      if (o instanceof Integer) {// type
          rs = new Packet<Object>(PacketUtils.getInstance().buildResponseHead(head, (int) o));
      } else if (o instanceof Head) {// head
          rs = new Packet<Object>((Head) o);
      } else if (o instanceof Packet) {
          rs = (Packet<?>) o;
      } else {// should be Packet body
          rs = new Packet<Object>(PacketUtils.getInstance().buildResponseHead(head, Type.RESPONSE_OK), o);
      }
      return rs;
  }
  
  private Object searchArg(Parameter p , String clientId, String username, Head head, JsonParser parser){
    if(p.getType().equals(Head.class)){
      return head;
    }
    if(p.isAnnotationPresent(MqttMessageBody.class)){
      try {
        return PacketUtils.getInstance().parseBody(parser, p.getType());
      } catch (IOException e) {
        throw new RuntimeException("json deserialize error", e);
      }
    }
    if(p.getType().equals(String.class)){
      MqttMessageParam annotation = p.getDeclaredAnnotation(MqttMessageParam.class);
      switch(annotation.value()){
        case clientId:
          return clientId; 
        case username:
          return username;
      }
    }
    return null;
  }

}
