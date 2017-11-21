package com.minivision.cameraplat.mqtt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Strategy;
import com.minivision.cameraplat.mqtt.RequestFuture;
import com.minivision.cameraplat.mqtt.message.MsgAnalyserConfig;
import com.minivision.cameraplat.mqtt.message.Packet;
import com.minivision.cameraplat.mqtt.message.PacketUtils;
import com.minivision.cameraplat.mqtt.message.Packet.Head;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Code;

@Component
public class AnalyserConfigService {
  
  @Autowired
  private PacketUtils packetUtils;
  
  @Autowired
  private PublishMessageTemplate publishMessageService;

  public void sendDeviceConfig(long analyserId, MsgAnalyserConfig config){
    asyncSendDeviceConfig(analyserId, config).getResponse().getBody();
  }
  
  public RequestFuture<Void> asyncSendDeviceConfig(long analyserId, MsgAnalyserConfig config){
    Head head = packetUtils.buildRequestHead(Code.SYNC_DEVICE);
    Packet<MsgAnalyserConfig> packet = new Packet<MsgAnalyserConfig>(head, config);
    return publishMessageService.sendRequest("/d/"+analyserId, packet, Void.class);
  }
  
  public void addOrUpdateCamera(Camera camera){
    asyncAddOrUpdateCamera(camera).getResponse().getBody();
  }
  
  public RequestFuture<Void> asyncAddOrUpdateCamera(Camera camera){
    MsgAnalyserConfig config = new MsgAnalyserConfig(camera);
    Head head = packetUtils.buildRequestHead(Code.UPDATE_CAMERA);
    Packet<MsgAnalyserConfig> packet = new Packet<MsgAnalyserConfig>(head, config);
    return publishMessageService.sendRequest("/d/"+camera.getAnalyser().getId(), packet, Void.class);
  }
  
  public void deleteCamera(Camera camera){
    asyncDeleteCamera(camera).getResponse().getBody();
  }
  
  public RequestFuture<Void> asyncDeleteCamera(Camera camera){
    Head head = packetUtils.buildRequestHead(Code.DEL_CAMERA);
    List<Long> ids = new ArrayList<>();
    ids.add(camera.getId());
    Map<String, List<Long>> body = new HashMap<>();
    body.put("id", ids);
    Packet<Map<String, List<Long>>> packet = new Packet<>(head, body);
    return publishMessageService.sendRequest("/d/"+camera.getAnalyser().getId(), packet, Void.class, false);
  }
  
  public void sendCameraStrategy(long analyserId, List<Strategy> strategy){
    asyncSendCameraStrategy(analyserId, strategy).getResponse().getBody();
  }
  
  public RequestFuture<Void> asyncSendCameraStrategy(long analyserId, List<Strategy> strategy){
    Head head = packetUtils.buildRequestHead(Code.STRATEGY_INFO);
    Packet<List<Strategy>> packet = new Packet<>(head, strategy);
    return publishMessageService.sendRequest("/d/"+analyserId, packet, Void.class);
  }
  
  
}
