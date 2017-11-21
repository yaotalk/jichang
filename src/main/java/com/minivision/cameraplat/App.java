package com.minivision.cameraplat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.minivision.cameraplat.mqtt.MqttStartup;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableJpaAuditing
public class App {
  
  public static void main(String[] args) throws Exception {
	ConfigurableApplicationContext ctx = SpringApplication.run(App.class, args);
	
	MqttStartup startup = ctx.getBean(MqttStartup.class);
	
	startup.start();
  }
  
}
