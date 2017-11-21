package com.minivision.cameraplat.mqtt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.cameraplat.domain.Analyser;
import com.minivision.cameraplat.domain.AnalyserStatus;
import com.minivision.cameraplat.domain.AnalyserStatus.CameraStatus;
import com.minivision.cameraplat.mqtt.message.Packet;
import com.minivision.cameraplat.mqtt.message.Packet.Head;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Code;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Type;

public class MqttSimulator {
  public static final String HOST = "tcp://localhost:1883";
  
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static final String TOPIC = "s/t";
  private static final String clientid = "8";

  private MqttClient client;
  private String userName = "sim";
  private String passWord = "test";


  public MqttSimulator() throws MqttException {
    client = new MqttClient(HOST, clientid, new MemoryPersistence());
    connect();
  }

  private void connect() {
    MqttConnectOptions options = new MqttConnectOptions();
    options.setCleanSession(false);
    options.setUserName(userName);
    options.setPassword(passWord.toCharArray());
    options.setConnectionTimeout(10);
    options.setKeepAliveInterval(20);
    
    try {
      client.setCallback(new MqttCallback() {
        @Override
        public void messageArrived(String topic, MqttMessage message)
            throws Exception {
          System.out.println("receive a message on : "+ topic);
          System.out.println(new String(message.getPayload()));
          ObjectMapper oMapper = new ObjectMapper();
          Packet<?> packet = oMapper.readValue(message.getPayload(), Packet.class);
          Head head = packet.getHead();
          head.setType(Type.RESPONSE_OK);
          Packet<Void> rePacket = new Packet<>();
          rePacket.setHead(head);
          publishObject("/s", rePacket);
        }
        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
          System.out.println("deliveryComplete :"+ token);
        }
        @Override
        public void connectionLost(Throwable paramThrowable) {

        }
      });
      client.connect(options);
      client.subscribe("/d/"+clientid);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void publish(String topic, MqttMessage message)
      throws MqttPersistenceException, MqttException {
    client.publish(topic, message);
  }
  
  public void publishObject(String topic, Object object)
      throws MqttPersistenceException, MqttException, JsonProcessingException {
    MqttMessage message = new MqttMessage();
    message.setPayload(MAPPER.writeValueAsBytes(object));
    client.publish(topic, message);
  }
  
  public void publishImage(String topic, byte[] image) throws MqttPersistenceException, MqttException{
    MqttMessage message = new MqttMessage();
    ByteBuffer buffer = ByteBuffer.allocate(128+image.length);
    buffer.putLong(10000001l);
    buffer.putLong(System.currentTimeMillis());
    buffer.putInt(22);
    buffer.putInt(image.length);
    buffer.position(128);
    buffer.put(image);
    message.setPayload(buffer.array());
    client.publish(topic, message);
  }

  public static void main(String[] args) throws MqttException, IOException, InterruptedException {
    MqttSimulator simulator = new MqttSimulator();
    
    AnalyserStatus status = new AnalyserStatus();
    status.setCpu(56f);
    status.setMem(60f);
    status.setTimestamp(new Date());
    Analyser analyser = new Analyser();
    analyser.setId(8l);
    status.setAnalyser(analyser);
    
    List<CameraStatus> devStatus = new ArrayList<>();
    CameraStatus cStatus = new CameraStatus();
    cStatus.setId(2);
    cStatus.setStatus(1);
    devStatus.add(cStatus);
    status.setDevStatus(devStatus);
    
    
    Head head = new Head(10000, Code.STATUS_INFO, Type.NOTIFY);
    
    Packet<?> packet = new Packet<>(head, status);
    
    simulator.publishObject("/s", packet);
    
/*    Head head1 = new Head(1000, Code.SYNC_DEVICE, Type.REQUEST, "");
    Packet<?> packet1 = new Packet<>(head1);
    simulator.publishObject("/s", packet1);
    
    Head head2 = new Head(1000, Code.STRATEGY_INFO, Type.REQUEST, "");
    Packet<?> packet2 = new Packet<>(head2);
    simulator.publishObject("/s", packet2);
    
    byte[] image = FileUtils.readFileToByteArray(new File("E://44.jpg"));
    simulator.publishImage("/s/i", image);
    */
  }
}
