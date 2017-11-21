package com.minivision.cameraplat.mqtt.handler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.minivision.cameraplat.domain.Analyser;
import com.minivision.cameraplat.domain.AnalyserStatus;
import com.minivision.cameraplat.domain.Strategy;
import com.minivision.cameraplat.domain.AnalyserStatus.CameraStatus;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.CodeHandler;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.MqttMessageBody;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.MqttMessageParam;
import com.minivision.cameraplat.mqtt.handler.MqttMessageHandler.MqttMessageParam.ParamType;
import com.minivision.cameraplat.mqtt.message.MsgAnalyserConfig;
import com.minivision.cameraplat.mqtt.message.MsgCameraStrategy;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Code;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Type;
import com.minivision.cameraplat.service.AnalyserService;
import com.minivision.cameraplat.service.AnalyserStatusService;
import com.minivision.cameraplat.service.CameraService;
import com.minivision.cameraplat.service.StrategyService;

@MqttMessageHandler
public class CodeMsgHandler {
  
  @Autowired
  private AnalyserStatusService statusService;
  
  @Autowired
  private AnalyserService analyserService;
  
  @Autowired
  private StrategyService strategyService;
  
  @Autowired
  private CameraService cameraService;
  
  @Autowired
  CameraImageHandler imageHandler;
  
  @CodeHandler(Code.STATUS_INFO)
  public void statusReport(@MqttMessageBody AnalyserStatus status, @MqttMessageParam(ParamType.clientId) String clientId){
    Analyser analyser = new Analyser();
    analyser.setId(Long.valueOf(clientId));
    status.setAnalyser(analyser);
    status.setTimestamp(new Date());
    List<CameraStatus> devStatus = status.getDevStatus();
    if(devStatus!=null && !devStatus.isEmpty()){
      for(CameraStatus s: devStatus){
        cameraService.updateOnlineStatus(s.getId(), s.getStatus());
      }
    }
    statusService.save(status);
  }
  
  @CodeHandler(code = Code.SYNC_DEVICE, type = Type.REQUEST)
  public MsgAnalyserConfig getDeviceInfo(@MqttMessageParam(ParamType.clientId) String clientId){
    Analyser analyser = analyserService.findById(Long.valueOf(clientId));
    MsgAnalyserConfig config = new MsgAnalyserConfig(analyser, analyser.getCameras());
    return config;
  }
  
  @CodeHandler(code = Code.STRATEGY_INFO, type = Type.REQUEST)
  public List<MsgCameraStrategy> getStratefyInfo(){
    List<Strategy> strategies = strategyService.findAll();
    return strategies.stream().map(s-> new MsgCameraStrategy(s)).collect(Collectors.toList());
  }
  
  @CodeHandler(code = Code.REC_COMPLETE, type = Type.NOTIFY)
  public void recognizedComplete(@MqttMessageBody Map<String, Integer> map){
    int cameraId = map.get("cameraId");
    int trackId = map.get("trackId");
    imageHandler.onRecComplete(cameraId, trackId);
  }
  
  @CodeHandler(code = Code.MULTI_FACE, type = Type.NOTIFY)
  public void multiFace(@MqttMessageBody Map<String, Integer> map){
    int cameraId = map.get("cameraId");
    imageHandler.onMultiFace(cameraId);
  }
  
}
