package com.minivision.cameraplat.mqtt;

import com.minivision.cameraplat.mqtt.auth.CamaraAuthenticator;
import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Properties;

@Component
public class MqttStartup {
	private static final Logger logger = LoggerFactory.getLogger(MqttStartup.class);
	
	@Autowired
	private CamaraAuthenticator camaraAuthenticator;
	
	@Autowired
	private Server mqttBroker;
	
	@Autowired
	private ServerInterceptHandler handler;
	
	public void start() throws Exception {
		startBroker();
	}
	
	@PreDestroy
	public void stop() throws Exception {
	  mqttBroker.stopServer();
      logger.info("MQTT Broker stoped");
	}

	private void startBroker() throws IOException {
		Properties props = new Properties();
		props.put(BrokerConstants.PORT_PROPERTY_NAME, Integer.toString(BrokerConstants.PORT));
		props.put(BrokerConstants.HOST_PROPERTY_NAME, BrokerConstants.HOST);
		props.put(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME, BrokerConstants.DISABLED_PORT_BIND);
		props.put(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, Boolean.FALSE.toString());
		mqttBroker.startServer(new MemoryConfig(props),null,null,camaraAuthenticator,null);
		mqttBroker.addInterceptHandler(handler);
		// Bind a shutdown hook
		/*Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				mqttBroker.stopServer();
				logger.info("MQTT Broker stoped");
			}
		});*/
		logger.info("MQTT Broker started");
	}
	
}
