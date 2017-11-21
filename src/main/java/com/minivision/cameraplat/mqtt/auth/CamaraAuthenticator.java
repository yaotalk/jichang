package com.minivision.cameraplat.mqtt.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.minivision.cameraplat.domain.Analyser;
import com.minivision.cameraplat.service.AnalyserService;

import io.moquette.spi.security.IAuthenticator;

@Component
public class CamaraAuthenticator implements IAuthenticator {
  private final static Logger logger = LoggerFactory.getLogger(CamaraAuthenticator.class);
  
  @Autowired
  private AnalyserService analysisService;
  
  @Override
  public boolean checkValid(String clientId, String username, byte[] password) {
    
    try{
      Analyser server = analysisService.findById(Long.valueOf(clientId));
      //Analyser server = analysisService.fingByUsername(username);
      if(server != null){
        boolean correct = server.getUsername().equals(username) && server.getPassword().equals(new String(password));
        if(!correct){
          logger.debug("camara server auth fail, clientId:{}, username: {}, password: {}",clientId, username, new String(password));
        }
        return correct;
      }
      logger.debug("unknow camara server, clientId: {}",clientId);
    }catch(Exception e){
      logger.error("camara server auth fail, clientId:{}, username: {}, password: {}",clientId, username, new String(password), e);
    }
    return false;
  }

}
