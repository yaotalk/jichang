package com.minivision.cameraplat.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.FaceSet;
import com.minivision.cameraplat.faceplat.client.FacePlatClient;
import com.minivision.cameraplat.faceplat.ex.FacePlatException;
import com.minivision.cameraplat.faceplat.result.detect.faceset.SetCreateResult;
import com.minivision.cameraplat.faceplat.result.detect.faceset.SetDeleteResult;
import com.minivision.cameraplat.faceplat.result.detect.faceset.SetDetailResult;
import com.minivision.cameraplat.faceplat.result.detect.faceset.SetModifyResult;
import com.minivision.cameraplat.mvc.ex.ServiceException;
import com.minivision.cameraplat.repository.CameraRepository;
import com.minivision.cameraplat.repository.FaceSetRepository;


@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
// TODO with redis
public class FaceSetServiceImpl implements FaceSetService {

  private final FaceSetRepository faceSetRepository;

//  private static final Logger log = LoggerFactory.getLogger(FaceSetServiceImpl.class);
  @Autowired
  private FacePlatClient facePlatClient;
  @Autowired
  private CameraRepository cameraRepository;

  /*@Value("${system.store.people}")
  private String filepath;*/

  public FaceSetServiceImpl(FaceSetRepository faceSetRepository) {
    this.faceSetRepository = faceSetRepository;
  }

  @Override
  public List<FaceSet> findAll() {
    List<FaceSet> faceSets = faceSetRepository.findAll();
    return faceSets;
  }

  @Override
  public List<FaceSet> findByFaceplat() {
    List<FaceSet> faceSets = faceSetRepository.findAll();
    for (FaceSet faceSet : faceSets)
      try {
        SetDetailResult setDetailResult = facePlatClient.getFaceSetDetail(faceSet.getToken());
        if (setDetailResult.getFacesetToken() != null) {
          faceSet.setFaceCount(setDetailResult.getFaceCount());
          if (faceSet.getName() == null) {
            faceSet.setName(setDetailResult.getDisplayName());
          }
        } else
          faceSet.setFaceCount(-1);
      } catch (Exception e) {
        log.error("fail to get faceSet detail from facePlat,catch exception {}",e.getMessage());
        faceSet.setFaceCount(-1);
      }
    return faceSets;
  }

  @Override
  public FaceSet update(FaceSet faceSet) throws ServiceException {
    Assert.notNull(faceSet, "faceSet must not be null");
    Assert.notNull(faceSet.getToken(), "faceSet must not be null");
    try {
      SetDetailResult setDetailResult = facePlatClient.getFaceSetDetail(faceSet.getToken());
      if(setDetailResult.getFaceCount() >  faceSet.getCapacity()){
        throw new ServiceException("faceSet capacity can not be less than faceSet count");
      }
      FaceSet oldFaceSet = faceSetRepository.findOne(faceSet.getToken());
      SetModifyResult modifyResult = facePlatClient.updateFaceset(faceSet);
      if (modifyResult != null && modifyResult.getFacesetToken() != null) {
        faceSet.setCreateTime(oldFaceSet.getCreateTime());
        return faceSetRepository.save(faceSet);
      }
    } catch (FacePlatException e) {
        throw  new ServiceException("update faceSet failed  "+e.getMessage());
    }
    return null;
  }

  public FaceSet create(FaceSet faceSet) {
    Assert.notNull(faceSet, "faceSet must not be null");
    SetCreateResult setCreateResult = facePlatClient.createFaceset(faceSet);
    if (setCreateResult != null && setCreateResult.getFacesetToken() != null) {
      faceSet.setToken(setCreateResult.getFacesetToken());
      return faceSetRepository.save(faceSet);
    }
    return null;
  }

  @Override
  public FaceSet find(String token) {
    Assert.notNull(token, "token must not be null");
    return faceSetRepository.findOne(token);
  }

  @Override
  public void delete(String token) throws ServiceException {
    Assert.notNull(token, "token must not be null");
    List<Camera> cameras = cameraRepository.findByfaceSetsToken(token);
    FaceSet faceSet = faceSetRepository.findOne(token);
    for (Camera camera : cameras) {
      camera.getFaceSets().remove(faceSet);
    }
    faceSetRepository.delete(token);
    /*try {
      FileUtils.deleteDirectory(new File(filepath + File.separator + token));
    } catch (IOException e) {
      log.error(e.getMessage());
    }*/
    try {
      SetDeleteResult deleteResult = facePlatClient.delFaceset(token, true);
      if (deleteResult == null || deleteResult.getFacesetToken() == null) {
        throw new ServiceException("fail to delete faceset on redis");
      }
    } catch (FacePlatException e) {
      if (!e.getMessage().contains("NOT_EXIST")) {
        throw new ServiceException("fail to delete faceset on redis");
      }

    }

  }

  @Override
  public Set<FaceSet> findAll(String ids) {
    Iterable<FaceSet> iterable = faceSetRepository.findAll(Arrays.asList(ids.split(",")));
    Set<FaceSet> set = new HashSet<FaceSet>();
    if (iterable != null) {
      Iterator<FaceSet> iterator = iterable.iterator();
      while (iterator.hasNext()) {
        set.add((FaceSet) iterator.next());
      }
    }
    return set;
  }

  @Override
  public Page<FaceSet> findAll(int page, int size) {
    Pageable pageable = new PageRequest(page, size);
    return faceSetRepository.findAll(pageable);
  }

}
