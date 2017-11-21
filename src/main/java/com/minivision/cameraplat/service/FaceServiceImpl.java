package com.minivision.cameraplat.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Face;
import com.minivision.cameraplat.domain.FaceSet;
import com.minivision.cameraplat.domain.FaceTmp;
import com.minivision.cameraplat.domain.record.MonitorRecord;
import com.minivision.cameraplat.faceplat.client.FacePlatClient;
import com.minivision.cameraplat.faceplat.ex.FacePlatException;
import com.minivision.cameraplat.faceplat.result.FailureDetail;
import com.minivision.cameraplat.faceplat.result.detect.DetectResult;
import com.minivision.cameraplat.faceplat.result.detect.DetectedFace;
import com.minivision.cameraplat.faceplat.result.detect.SearchResult;
import com.minivision.cameraplat.faceplat.result.detect.SearchResult.Result;
import com.minivision.cameraplat.faceplat.result.detect.faceset.AddFaceResult;
import com.minivision.cameraplat.faceplat.result.detect.faceset.RemoveFaceResult;
import com.minivision.cameraplat.mvc.ex.ServiceException;
import com.minivision.cameraplat.param.PageParam;
import com.minivision.cameraplat.repository.CameraRepository;
import com.minivision.cameraplat.repository.FaceRepository;
import com.minivision.cameraplat.repository.mongo.MonitorRecordRepository;
import com.minivision.cameraplat.rest.param.alarm.AlarmFaceParam;
import com.minivision.cameraplat.rest.param.faceset.FaceSearchParam;
import com.minivision.cameraplat.rest.param.faceset.FacesetParam;
import com.minivision.cameraplat.rest.result.alarm.AlarmFacesResult;
import com.minivision.cameraplat.rest.result.faceset.FaceSearchResult;
import com.minivision.cameraplat.util.ChunkRequest;

@Transactional
@Service
public class FaceServiceImpl implements FaceService {

  private Logger logger = LoggerFactory.getLogger(FaceServiceImpl.class);
  @Autowired
  private FaceRepository faceRepository;

  @Autowired
  private FacePlatClient facePlatClient;

  @PersistenceContext
  private EntityManager manager;

  @Autowired
  private MonitorRecordRepository monitorRecordRepository;
  @Autowired
  private CameraRepository cameraRepository;

  /*@Value("${system.store.people}")
  private String filepath;*/

  /*@Value("${system.store.snapshot}")
  private String snapfilepath;*/

  @Autowired
  private FastDFSService fileService;

  private void save(Face face, String faceToken, String facesetToken, byte[] imgData,
      String fileType) throws ServiceException {
    face.setId(faceToken);
    // 对于需要保存的图片，计算md5
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(imgData);
      BigInteger bi = new BigInteger(1, md5.digest());
      face.setPicMd5(bi.toString(16));
    } catch (NoSuchAlgorithmException e1) {
      logger.error("计算文件的md5时异常", e1);
    }

    /*String fileName = facesetToken + "/" + faceToken + fileType;
    File imgfile = new File(filepath, fileName);*/
    String fileName = faceToken + fileType;
	String fileIdBase64 = "";
    try {
      //FileUtils.writeByteArrayToFile(imgfile, imgData);
      fileIdBase64 = fileService.uploadToFastDFS(fileName, imgData);
    } catch (Throwable e) {
      throw new ServiceException("image file upload fail", e);
    }

    AddFaceResult addFaceResult = facePlatClient.addFace(facesetToken, faceToken);

    if (addFaceResult.getFacesetToken() == null) {
      throw new ServiceException("add face to faceset fail");
    }
    if (addFaceResult.getFailureDetail() != null) {
      FailureDetail failureDetail = addFaceResult.getFailureDetail().get(0);
      logger.warn("add face[{}] to faceset[{}] fail, {}", failureDetail.getFaceToken(),
          facesetToken, failureDetail.getReason());
      throw new ServiceException("add face to faceset fail, " + failureDetail.getReason());
    }

    face.setImgpath(fileIdBase64);
    face.setImgUrl(fileService.getFileUrl(fileIdBase64));
    face.setFaceSet(new FaceSet(facesetToken));
    //face.setPicSize((int) imgfile.length());
    face.setPicSize(imgData.length);
    faceRepository.save(face);
  }


  /**
   * 保存人脸
   */
  @Override
  public void save(Face face, String facesetToken, byte[] imgData, String fileType)
      throws ServiceException {
    DetectResult detectResult = facePlatClient.detect(imgData, true);
    List<DetectedFace> faces = detectResult.getFaces();
    if (faces.isEmpty()) {
      throw new ServiceException("no face detected");
    }
    DetectedFace detectedFace = faces.get(0);
    
    if(face.getSex() == null) {
      face.setSex(detectedFace.getFaceAttribute().getGender() == 1? "1": "0");
    }
    save(face, detectedFace.getFaceToken(), facesetToken, imgData, fileType);
  }

  @Override
  public void save(String faceToken, String facesetToken, byte[] imgData) throws ServiceException {
    Face face = new Face();
    save(face, faceToken, facesetToken, imgData, ".jpg");
  }

  @Override
  public void update(Face face) {
    Face oldface = faceRepository.findOne(face.getId());
    face.setFaceSet(oldface.getFaceSet());
    faceRepository.save(face);
  }



  /**
   * 删除人脸
   * 
   * @param facesetToken
   * @param faceToken
   */
  public void delete(String facesetToken, String faceTokens) {
    RemoveFaceResult removeFaceResult = facePlatClient.removeFace(facesetToken, faceTokens);
    if (removeFaceResult.getFailureDetail() == null) {
      /*for (String faceToken : faceTokens.split(",")) {
        String imgPath = filepath + File.separator + faceRepository.findOne(faceToken).getImgpath();
        try {
          File file = new File(imgPath);
          file.delete();
        } catch (Exception e) {
          logger.error(e.getMessage());
        }
      }*/
      faceRepository.deleteByIdIn(Arrays.asList(faceTokens.split(",")));
    }
  }

  @Override
  public Face find(String faceToken) {
    return faceRepository.findOne(faceToken);
  }

  @Override
  public Page<Face> findByPage(PageParam pageParam, String facesetToken) {
    Pageable pageable = new ChunkRequest(pageParam.getOffset(), pageParam.getLimit());
    return faceRepository.findAll((root, criteriaQuery, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      Path<FaceSet> path = root.get("faceSet");
      predicates.add(cb.like(path.get("token").as(String.class), facesetToken));
      String search = pageParam.getSearch();
      if (search != null && !"".equals(search.trim())) {
        Predicate name = cb.like(root.get("name").as(String.class), "%" + search + "%");
        Predicate employeeId = cb.like(root.get("employeeId").as(String.class), "%" + search + "%");
        Predicate idCard = cb.like(root.get("idCard").as(String.class), "%" + search + "%");
        Predicate phoneNumber =
            cb.like(root.get("phoneNumber").as(String.class), "%" + search + "%");
        Predicate sex = null;
        Predicate p4;
        if ("男".equals(search)) {
          sex = cb.equal(root.get("sex"), "1");
        } else if ("女".equals(search)) {
          sex = cb.equal(root.get("sex"), "0");
        }
        if (sex != null) {
          p4 = cb.or(name, idCard, phoneNumber, employeeId, sex);
        } else
          p4 = cb.or(name, idCard, phoneNumber, employeeId);
        predicates.add(p4);
      }
      Predicate[] p = new Predicate[predicates.size()];
      return cb.and(predicates.toArray(p));
    }, pageable);
  }

  @Override
  public Page<Face> findByFacesetId(FacesetParam facesetParam) {

    Pageable pageable = new ChunkRequest(facesetParam.getOffset(), facesetParam.getLimit());
    return faceRepository.findByFaceSetToken(facesetParam.getFacesetToken(), pageable);

  }

  @Override
  public List<FaceSearchResult> searchByPlat(FaceSearchParam faceSearchParam)
      throws ServiceException {
    List<Result> results = null;
    try {
      List<FaceSearchResult> fsr = new ArrayList<>();
      List<FaceSearchResult> faceSearchResults = new ArrayList<>();
      for (String facesetToken : faceSearchParam.getFacesetTokens().split(",")) {
        SearchResult searchResult = facePlatClient.search(facesetToken,
            faceSearchParam.getImgfile().getBytes(), faceSearchParam.getLimit());
        if (searchResult.getResults() != null) {
          results = searchResult.getResults();
          for (Result result : results) {
            double confidence = result.getConfidence();
            if ((double) faceSearchParam.getThreshold() / 100 <= confidence) {
              String faceToken = result.getFaceToken();
              Face face = faceRepository.findOne(faceToken);
              FaceSearchResult fs = new FaceSearchResult();
              if (face != null) {
                fs.setFacesetToken(face.getFaceSet().getToken());
                fs.setFaceToken(faceToken);
                fs.setName(face.getName());
                fs.setSex(face.getSex());
                fs.setIdCard(face.getIdCard());
                //fs.setImgpath(face.getImgpath());
				fs.setImgpath(face.getImgUrl());
                fs.setConfidence(confidence);
                fs.setEmployeeId(face.getEmployeeId());
                fsr.add(fs);
              }
            }
          }
        }
      }
      fsr.stream().sorted(Comparator.comparing(FaceSearchResult::getConfidence).reversed())
          .limit(faceSearchParam.getLimit()).forEach(faceSearchResults::add);
      return faceSearchResults;
    } catch (IOException e) {
      throw new ServiceException("File upload failed");
    } catch (FacePlatException e) {
      throw new ServiceException(e.getMessage());
    }
  }

  @Override
  public List<AlarmFacesResult> searchByFlatForAlarm(AlarmFaceParam alarmFaceParam) {
    MonitorRecord monitorRecord = monitorRecordRepository.findOne(alarmFaceParam.getLogid());
    /*String snapPath =
        snapfilepath + File.separator + monitorRecord.getSnapshot().getPhotoFileName();*/
    try {
      //byte[] bytes = FileUtils.readFileToByteArray(new File(snapPath));
      byte[] bytes = fileService.downloadFromFastDFS(monitorRecord.getSnapshot().getPhotoFileName());
      Camera camera = cameraRepository.findOne(monitorRecord.getSnapshot().getCameraId());
      List<AlarmFacesResult> afrs = new ArrayList<>();
      List<AlarmFacesResult> alarmFacesResults = new ArrayList<>();
      for (FaceSet faceSet : camera.getFaceSets()) {
        List<Result> results = null;
        SearchResult searchResult =
            facePlatClient.search(faceSet.getToken(), bytes, alarmFaceParam.getLimit());
        if (searchResult.getResults() != null) {
          results = searchResult.getResults();
          for (Result result : results) {
            double confidence = result.getConfidence();
            String faceToken = result.getFaceToken();
            Face face = faceRepository.findOne(faceToken);
            AlarmFacesResult afr = new AlarmFacesResult();
            if (face != null) {
              afr.setId(face.getId());
              afr.setName(face.getName());
              //afr.setUserImgUrl(face.getImgpath());
              afr.setUserImgUrl(face.getImgUrl());
              afr.setConfidence(confidence);
              afr.setEmployeeId(face.getEmployeeId());
              afrs.add(afr);
            }
          }
        }
      }
      afrs.stream().sorted(Comparator.comparing(AlarmFacesResult::getConfidence).reversed())
          .limit(alarmFaceParam.getLimit()).forEach(alarmFacesResults::add);
      return alarmFacesResults;
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public List<Face> getRepeatFaceBySetToken(String faceSetToken, PageParam pageParam) {
    String sql =
        "select t1.id,t1.`name`,t1.sex,t1.id_card,t1.employee_id,t1.pic_size,t1.pic_md5,t1.imgpath,t1.phone_number,t1.faceset_id,t1.create_time,t1.img_url from `face` as t1,"
            + " (select count(1),ta.pic_md5 from  `face` as ta,"
            + " (SELECT count(1),t.pic_size FROM `face` AS t where t.faceset_id=:faceSetToken  and t.pic_size is not null and t.pic_md5 is not null"
            + " group by t.pic_size HAVING count(1)>1) as tb"
            + " where ta.pic_size=tb.pic_size  and ta.faceset_id=:faceSetToken group by  pic_md5 having count(1)>1) as t2 where t1.pic_md5=t2.pic_md5 "
            + "and t1.faceset_id=:faceSetToken order by t1.pic_md5";

    Query query = manager.createNativeQuery(sql, Face.class);
    query.setParameter("faceSetToken", faceSetToken);
    int startPosition = pageParam.getOffset();
    int maxResult = pageParam.getLimit();
    query.setFirstResult(startPosition);
    query.setMaxResults(maxResult);
    return query.getResultList();
  }

  @Override
  public int getRepeatFaceCoutBySetToken(String faceSetToken) {
    String sql =
        "select count(1) from `face` as t1," + " (select count(1),ta.pic_md5 from  `face` as ta,"
            + " (SELECT count(1),t.pic_size FROM `face` AS t where t.faceset_id=:faceSetToken  and t.pic_size is not null and t.pic_md5 is not null"
            + " group by t.pic_size HAVING count(1)>1) as tb"
            + " where ta.pic_size=tb.pic_size  and ta.faceset_id=:faceSetToken group by  pic_md5 having count(1)>1) as t2 where t1.pic_md5=t2.pic_md5 "
            + "and t1.faceset_id=:faceSetToken order by t1.pic_md5";
    Query query = manager.createNativeQuery(sql);
    query.setParameter("faceSetToken", faceSetToken);
    return ((BigInteger) query.getSingleResult()).intValue();
  }

  @Override
  public int checkIfHasNotDetected(String faceSetToken) {
    String sql =
        "select count(1) from face as t where (t.pic_md5 is null or t.pic_size is null) and t.faceset_id=:faceSetToken";
    Query query = manager.createNativeQuery(sql);
    query.setParameter("faceSetToken", faceSetToken);
    return ((BigInteger) query.getSingleResult()).intValue();
  }

  @Override
  public List<Face> getNotDetectedData(String faceSetToken, PageParam pageParam) {
    String sql =
        "select t.id,t.`name`,t.sex,t.id_card,t.employee_id,t.pic_size,t.pic_md5,t.imgpath,t.phone_number,t.faceset_id,t.create_time,t.img_url"
            + " from face as t where (t.pic_md5 is null or t.pic_size is null) and t.faceset_id=:faceSetToken";
    Query query = manager.createNativeQuery(sql, Face.class);
    query.setParameter("faceSetToken", faceSetToken);
    int startPosition = pageParam.getOffset();
    int maxResult = pageParam.getLimit();
    query.setFirstResult(startPosition);
    query.setMaxResults(maxResult);
    return query.getResultList();
  }

  @Override
  public List<Face> getToProcess(String taskId, PageParam pageParam, boolean ifOrderBy) {
    String sql = "select ta.id,ta.`name`,ta.sex,ta.id_card,ta.employee_id,ta.pic_size,ta.create_time,ta.img_url,"
        + "ta.pic_md5,ta.imgpath,ta.phone_number,ta.faceset_id,tb.is_store from face as "
        + "ta right join face_tmp as tb on (ta.id=tb.pic_id) where tb.task_id=:taskId";
    String searchName = pageParam.getSearch();
    if (StringUtils.isNotBlank(pageParam.getSearch())) {
      sql = sql + " and ta.`name` like '%" + searchName + "%'";
    }
    if (ifOrderBy) {
      sql = sql + " order by ta.pic_md5,tb.is_store desc";
    }
    Query query = manager.createNativeQuery(sql, Face.class);
    query.setParameter("taskId", taskId);
    int startPosition = pageParam.getOffset();
    int maxResult = pageParam.getLimit();
    query.setFirstResult(startPosition);
    query.setMaxResults(maxResult);
    return query.getResultList();
  }

  @Override
  public List<Face> getToProcess(String taskId, PageParam pageParam, boolean ifOrderBy,
      boolean isIncludeStore) {
    String sql = "select ta.id,ta.`name`,ta.sex,ta.id_card,ta.employee_id,ta.pic_size,"
        + "ta.pic_md5,ta.imgpath,ta.img_url,ta.phone_number,ta.faceset_id,tb.is_store from face as "
        + "ta right join face_tmp as tb on (ta.id=tb.pic_id) where tb.task_id=:taskId";
    String searchName = pageParam.getSearch();
    if (StringUtils.isNotBlank(searchName)) {
      sql = sql
          + " and (ta.`name` like :searchName or ta.id_card like :searchName or ta.employee_id like :searchName or ta.pic_md5 like :searchName)";
    }
    if (ifOrderBy) {
      sql = sql + " order by ta.pic_md5,tb.is_store desc";
    }
    Query query = manager.createNativeQuery(sql);
    query.setParameter("taskId", taskId);
    if (StringUtils.isNotBlank(searchName)) {
      query.setParameter("searchName", "%" + searchName + "%");
    }
    int startPosition = pageParam.getOffset();
    int maxResult = pageParam.getLimit();
    query.setFirstResult(startPosition);
    query.setMaxResults(maxResult);
    List<Object[]> resultList = query.getResultList();
    Face[] faceList = new Face[resultList.size()];
    for (int i = 0; i < resultList.size(); i++) {
      faceList[i] = new Face();
      faceList[i].setId((String) resultList.get(i)[0]);
      faceList[i].setName((String) resultList.get(i)[1]);
      faceList[i].setSex((String) resultList.get(i)[2]);
      faceList[i].setIdCard(((String) resultList.get(i)[3]));
      faceList[i].setEmployeeId(((String) resultList.get(i)[4]));
      faceList[i].setPicSize(((Integer) resultList.get(i)[5]));
      faceList[i].setPicMd5((String) resultList.get(i)[6]);
      faceList[i].setImgpath((String) resultList.get(i)[7]);
      faceList[i].setImgUrl((String) resultList.get(i)[8]);
      faceList[i].setPhoneNumber((String) resultList.get(i)[9]);
    }

    return Arrays.asList(faceList);
  }

  @Override
  public void deleteTaskData(String taskId) {
    if (taskId != null) {
      String sql = "delete from face_tmp where task_id=:taskId";
      Query query = manager.createNativeQuery(sql);
      query.setParameter("taskId", taskId);
      query.executeUpdate();
    } else {
      String sql = "truncate table face_tmp";
      Query query = manager.createNativeQuery(sql);
      query.executeUpdate();
    }
  }

  @Override
  public void save(List<FaceTmp> faceTmpList) {
    for (FaceTmp faceTmp : faceTmpList) {
      manager.persist(faceTmp);
    }
    manager.flush();
    manager.clear();
  }

  @Override
  public void saveTmp(List<FaceTmp> faceTmpList) {
    String sql =
        "update face_tmp as t set t.is_store=:isStore where t.task_id=:taskId  and t.pic_id=:picId";
    Query query = manager.createNativeQuery(sql);
    for (FaceTmp faceTmp : faceTmpList) {
      query.setParameter("taskId", faceTmp.getTaskId());
      query.setParameter("isStore", faceTmp.getIsStore());
      query.setParameter("picId", faceTmp.getPicId());
      query.executeUpdate();
    }
  }


  @Override
  public void saveOnly(List<Face> faceList) {
    for (Face face : faceList) {
      manager.merge(face);
    }
    manager.flush();
    manager.clear();
  }

  @Override
  public int getToProcessNum(String taskId, PageParam pageParam) {
    String sql = "select count(1) from face as "
        + "ta right join face_tmp as tb on (ta.id=tb.pic_id) where tb.task_id=:taskId";
    String searchName = pageParam.getSearch();
    if (StringUtils.isNotBlank(searchName)) {
      sql = sql
          + " and (ta.`name` like :searchName or ta.id_card like :searchName or ta.employee_id like :searchName or ta.pic_md5 like :searchName)";
    }
    Query query = manager.createNativeQuery(sql);
    query.setParameter("taskId", taskId);
    if (StringUtils.isNotBlank(searchName)) {
      query.setParameter("searchName", "%" + searchName + "%");
    }
    return ((BigInteger) query.getSingleResult()).intValue();
  }

  @Override
  public boolean isCanSaveStore(FaceTmp faceTmp) {
    String sql = "select count(1) from face as t1, (select tf.pic_md5 from face as tf where "
        + "tf.id=:picId) as t2,face_tmp as t3 where t1.pic_md5=t2.pic_md5 and "
        + "t3.task_id=:taskId and t3.pic_id=t1.id and " + "t3.pic_id!=:picId and t3.is_store=1";

    Query query = manager.createNativeQuery(sql);
    query.setParameter("taskId", faceTmp.getTaskId());
    query.setParameter("picId", faceTmp.getPicId());
    int hasCount = ((BigInteger) query.getSingleResult()).intValue();
    if (hasCount > 0) {
      return false;
    } else {
      return true;
    }
  }



}
