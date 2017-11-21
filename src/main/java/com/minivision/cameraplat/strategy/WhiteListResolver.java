package com.minivision.cameraplat.strategy;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.MonitorImage;
import com.minivision.cameraplat.domain.record.SnapshotRecord;

public class WhiteListResolver extends StrategyResolver{

  @Override
  public void onRecognized(Camera camera, MonitorImage image, SnapshotRecord snapshotRecord,
      String faceToken) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onStranger(Camera camera, MonitorImage image, SnapshotRecord snapshotRecord) {
    // TODO Auto-generated method stub
    
  }

}
