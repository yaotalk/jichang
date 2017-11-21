package com.minivision.cameraplat.config;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.moquette.server.Server;

@Configuration
public class MqttConfig {

  @Bean
  public Server mqttServer(){
    return new Server();
  }
  
  @Bean
  public ObjectMapper objectMapper(){
    ObjectMapper om = new ObjectMapper();
    om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    om.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    return om;
  }
}
