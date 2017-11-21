package com.minivision.cameraplat.service;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Region;
import com.minivision.cameraplat.mvc.ex.ServiceException;
import com.minivision.cameraplat.rest.result.system.CameraResult;
import com.minivision.cameraplat.service.CameraServiceImpl.CameraShow;

import java.util.List;
import java.util.Set;

public interface CameraService {

    List<Camera> findAll();

    List<CameraShow> findAllWithStatus();

    Camera update(Camera camera) throws ServiceException;

    Camera create(Camera camera) throws ServiceException;

    void delete(Camera camera);

    Camera findByid(long id);

    Set<Camera> findByIds(List<Long> Ids);

    List<Camera> findByRegion(Region region);

    List<Camera> findByStategy(Long strategyId);

    boolean isOnline(long cameraId);
    
    void updateOnlineStatus(long cameraId, int status);

    List<CameraResult> findAllCameraResults();

}
