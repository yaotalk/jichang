package com.minivision.cameraplat.mqtt;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.stereotype.Component;

import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler;
import com.minivision.cameraplat.mqtt.handler.TopicHandlerMethodContext;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.TopicHandler;
import com.minivision.cameraplat.service.AnalyserService;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;

@Component
public class ServerInterceptHandler implements InterceptHandler{
  
  private final Logger logger = LoggerFactory.getLogger(ServerInterceptHandler.class);
  
  @Autowired
  private AnalyserService analyserService;
  
  @Autowired
  private ApplicationContext context;
  
  private Map<String, TopicHandlerMethodContext> topicHandlers = new HashMap<>();
  
  private ExecutorService exxcutor;
  
  //private int maxThreads = 4;
  
  @PostConstruct
  private void init(){
    exxcutor = Executors.newCachedThreadPool();
    
    Map<String, Object> beans = context.getBeansWithAnnotation(MqttMessageHandler.class);

    for (Object instance : beans.values()) {
      StandardAnnotationMetadata s = new StandardAnnotationMetadata(instance.getClass());
      Set<MethodMetadata> methods = s.getAnnotatedMethods(TopicHandler.class.getName());
      for (MethodMetadata t : methods) {
        StandardMethodMetadata m = (StandardMethodMetadata) t;
        Map<String, Object> methodMeta = m.getAnnotationAttributes(TopicHandler.class.getName());
        String topic = (String) methodMeta.get("value");
        Method h = m.getIntrospectedMethod();
        h.setAccessible(true);
        logger.info("Found data report handler method: {}, meta={}", h, methodMeta);
        TopicHandlerMethodContext context = new TopicHandlerMethodContext(instance, h);
        topicHandlers.put(topic, context);
      }
    }
  }

  @Override
  public void onConnect(InterceptConnectMessage connectMessage) {
    String id = connectMessage.getClientID();
    analyserService.online(Long.valueOf(id));
  }

  @Override
  public void onDisconnect(InterceptDisconnectMessage disconnectMessage) {
    String id = disconnectMessage.getClientID();
    analyserService.offline(Long.valueOf(id));
  }

  @Override
  public void onConnectionLost(InterceptConnectionLostMessage connectionLostMessage) {
    String id = connectionLostMessage.getClientID();
    analyserService.offline(Long.valueOf(id));
  }

  @Override
  public void onPublish(InterceptPublishMessage publishMessage) {
    
    
    logger.trace("receive a mqtt message: {}", publishMessage);
    String topicName = publishMessage.getTopicName();
    TopicHandlerMethodContext methodContext = topicHandlers.get(topicName);
    if(methodContext == null){
      //TODO log
      logger.warn("no handler method for topic: {}", topicName);
      return;
    }
    exxcutor.submit(new Runnable() {
      @Override
      public void run() {
        methodContext.process(publishMessage);
      }
    });
  }

  @Override
  public void onSubscribe(InterceptSubscribeMessage subscribeMessage) {
    logger.trace("subscribe message: client[{}], topic[{}]", subscribeMessage.getClientID(),subscribeMessage.getTopicFilter());
  }

  @Override
  public void onUnsubscribe(InterceptUnsubscribeMessage unsubscribeMessage) {
    logger.trace("unsubscribe message: client[{}], topic[{}]", unsubscribeMessage.getClientID(), unsubscribeMessage.getTopicFilter());
  }

  @Override
  public void onMessageAcknowledged(InterceptAcknowledgedMessage acknowledgedMessage) {
    logger.trace("acknowledge message: msg[{}], topic[{}]", acknowledgedMessage.getMsg(), acknowledgedMessage.getTopic());
  }

}
