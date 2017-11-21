package com.minivision.cameraplat.mqtt.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.minivision.cameraplat.domain.Camera;
import com.minivision.cameraplat.domain.Face;
import com.minivision.cameraplat.domain.FaceSet;
import com.minivision.cameraplat.domain.MonitorImage;
import com.minivision.cameraplat.domain.Strategy;
import com.minivision.cameraplat.domain.record.MonitorRecord;
import com.minivision.cameraplat.domain.record.MonitorRecord.FaceInfo;
import com.minivision.cameraplat.domain.record.SnapshotRecord;
import com.minivision.cameraplat.faceplat.client.FacePlatClient;
import com.minivision.cameraplat.faceplat.ex.FacePlatException;
import com.minivision.cameraplat.faceplat.result.detect.DetectResult;
import com.minivision.cameraplat.faceplat.result.detect.DetectedFace;
import com.minivision.cameraplat.faceplat.result.detect.SearchResult;
import com.minivision.cameraplat.faceplat.result.detect.SearchResult.Result;
import com.minivision.cameraplat.mqtt.message.Packet;
import com.minivision.cameraplat.mqtt.message.Packet.Head;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Code;
import com.minivision.cameraplat.mqtt.message.PacketUtils;
import com.minivision.cameraplat.mqtt.service.PublishMessageTemplate;
import com.minivision.cameraplat.mvc.ex.ServiceException;
import com.minivision.cameraplat.repository.mongo.MonitorRecordRepository;
import com.minivision.cameraplat.repository.mongo.SnapshotRecordRepository;
import com.minivision.cameraplat.service.CameraService;
import com.minivision.cameraplat.service.FaceService;
import com.minivision.cameraplat.service.FastDFSService;

@Component
public class CameraImageHandler {
	@Autowired
	private CameraService cameraService;
	@Autowired
	private FacePlatClient facePlatClient;
	@Autowired
	private FaceService faceService;
	@Autowired
	private MonitorRecordRepository monitorRepository;
	@Autowired
	private SnapshotRecordRepository snapshotRespository;
	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private PublishMessageTemplate mqttTemplate;

	/*@Value("${system.store.snapshot}")
	private String imageFilePath = ".";*/

	private String imageFileType = "jpg";

	private static final Logger logger = LoggerFactory.getLogger(CameraImageHandler.class);

	private Map<String, Long> lastSaveTime = new ConcurrentHashMap<>();

	private Map<Long, TempRecognizedResult> tempRecognizedResults = new ConcurrentHashMap<>();

	@Autowired
	private FastDFSService fileService;

	private void saveMonitorImage(MonitorImage image){
		if(image.getFileName() == null){
			//String fileName = image.getSerialNo() + "_" + image.getCameraId() + "_" + getDatePath() + "." + imageFileType;
			//String fileName = "camera_"+image.getCameraId() + "/" + CommonUtils.getDatePath() + "/" + image.getSerialNo() + "." + imageFileType;
			//File imgFile = new File(imageFilePath, fileName);
			String fileName = image.getSerialNo() + "." + imageFileType;
			try {
				//FileUtils.touch(imgFile);
				//FileUtils.writeByteArrayToFile(imgFile, image.getImage());
				image.setFileName(fileService.uploadToFastDFS(fileName, image.getImage()));
				image.setFileUrl(fileService.getFileUrl(image.getFileName()));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private void onRecognized(Camera camera, MonitorImage image, SnapshotRecord snapshotRecord, String faceToken){
		logger.info("reconized a face[{}], confidence:{}", faceToken, snapshotRecord.getConfidence());
		//黑名单命中
		if(camera.getStrategy().getType() == Strategy.StrategyType.BlackList || camera.getStrategy().getType() == Strategy.StrategyType.DynaminFaceRepo){
			int intervals = camera.getStrategy().getIntervals();
			saveSnapShot(image, snapshotRecord);
			Long lastSave = lastSaveTime.get(faceToken);
			if(lastSave == null){
				lastSave = 0l;
			}
			long now = System.currentTimeMillis();

			//now小于lastSave时，说明系统时间有更改
			if(now - lastSave < 0 || now - lastSave > intervals * 1000){
				Face face = faceService.find(faceToken);
				saveAlarm(snapshotRecord, face);
				lastSaveTime.put(faceToken, now);
				logger.info("get a record, face:{}", face);
			}else{
				Face face = faceService.find(faceToken);
				logger.info("discard a record, face:{}, lastSaveTime:{}",  face, new Date(lastSave));
			}
		}else if(camera.getStrategy().isSnapshot()){
			saveSnapShot(image, snapshotRecord);
		}

	}

	private void onStranger(Camera camera, MonitorImage image, SnapshotRecord snapshotRecord, String faceToken){
		logger.info("detected a stranger, confidence:{}", snapshotRecord.getConfidence());
		if(camera.getStrategy().getType() == Strategy.StrategyType.DynaminFaceRepo){
			Set<FaceSet> faceSets = camera.getFaceSets();
			Assert.isTrue(!faceSets.isEmpty(), "Create a faceset first!");
			FaceSet set = faceSets.iterator().next();
			try {
				faceService.save(faceToken, set.getToken(), image.getImage());
				saveSnapShot(image, snapshotRecord);
				Face face = faceService.find(faceToken);
				saveAlarm(snapshotRecord, face);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			return;
		}


		//白名单未命中底库
		if(camera.getStrategy().getType() == Strategy.StrategyType.WhiteList){
			saveSnapShot(image, snapshotRecord);
			saveAlarm(snapshotRecord, null);
		}else if(camera.getStrategy().isSnapshot()){
			saveSnapShot(image, snapshotRecord);
		}
	}

	private void saveSnapShot(MonitorImage image, SnapshotRecord snapshotRecord){
		saveMonitorImage(image);
		snapshotRecord.setPhotoFileName(image.getFileName());
		snapshotRecord.setPhotoUrl(image.getFileUrl());
		snapshotRespository.save(snapshotRecord);
		template.convertAndSend("/c/snapshot", snapshotRecord);
	}

	private void saveAlarm(SnapshotRecord snapshotRecord, Face face){
		MonitorRecord record = new MonitorRecord(snapshotRecord);
		if(face!=null){
		  FaceInfo faceInfo = new FaceInfo(face);
		  record.setFace(faceInfo);
		}
		monitorRepository.save(record);
		template.convertAndSend("/c/alarm", record);
	}

	public void onMultiFace(int cameraId){
		logger.info("multiFace, cameraId: {}", cameraId);
		Map<String, Object> data = new HashMap<>();
		data.put("cameraId", cameraId);
		template.convertAndSend("/c/multiFace", data);
	}

	public void onRecComplete(int cameraId, int trackId){
		long key = (long) cameraId << 32 | trackId;
		TempRecognizedResult temp = tempRecognizedResults.remove(key);
		if(temp!=null){
			onStranger(temp.camera, temp.image, temp.snapshotRecord, temp.getFaceToken());
		}
	}

	public void onImageReceive(MonitorImage image, String clientId) throws FacePlatException{

		int cameraId = image.getCameraId();
		int trackId = image.getTrackId();

		Camera camera = cameraService.findByid(cameraId);
		if(camera == null){
			return;
		}

		DetectResult detectResult = facePlatClient.detect(image.getImage(), true);
		if(detectResult.getFaces() == null || detectResult.getFaces().isEmpty()){
			//未检测到人脸
			return;
		}

		DetectedFace detectedFace = detectResult.getFaces().get(0);
		Strategy strategy = camera.getStrategy();
		Set<FaceSet> faceSets = camera.getFaceSets();
		double threshold = strategy.getCompareThreshold();

		SnapshotRecord snapshotRecord = new SnapshotRecord(image, detectedFace);

		double confidence = 0;
		String faceToken = null;
		String faceSetToken = null;
		if(faceSets != null && !faceSets.isEmpty()){
			for(FaceSet fSet: faceSets){
				SearchResult result = facePlatClient.search(fSet.getToken(), detectedFace.getFaceToken());
				List<Result> list = result.getResults();
				if(list!=null && list.size()>0){
					double thisConfidence = list.get(0).getConfidence()*100;
					if(thisConfidence > confidence){
						confidence = thisConfidence;
						faceToken = list.get(0).getFaceToken();
						faceSetToken = fSet.getToken();
					}
				}
			}

			snapshotRecord.setConfidence((float) confidence);
			logger.info("detect a face[{}] in faceset[{}], confidence:{}", faceToken, faceSetToken, confidence);
			//识别成功
			if(confidence > threshold){
				//notify camera

				Head head = PacketUtils.getInstance().buildNotifyHead(Code.REC_INFO);
				Map<String, Object> body = new HashMap<>();
				body.put("cameraId", cameraId);
				body.put("trackId", trackId);
				body.put("recResult", 1);

				Packet<Map<String, Object>> packet = new Packet<>(head, body);
				mqttTemplate.sendTo("/d/"+clientId, packet, false);

				onRecognized(camera, image, snapshotRecord, faceToken);
				tempRecognizedResults.remove(trackId);
				return;
			}

		}


		//陌生人  
		long key = (long)cameraId << 32 | trackId;

		TempRecognizedResult result = tempRecognizedResults.get(key);
		if(result == null){
			result = new TempRecognizedResult(camera);
			tempRecognizedResults.put(key, result);
		}
		int times = result.addTimes();
		if(times >= strategy.getRetryCounts()){
			//重试后确认为陌生人
			onStranger(camera, image, snapshotRecord, detectedFace.getFaceToken());
			tempRecognizedResults.remove(key);

		}else{
			logger.info("stranger: trackId:{}, times:{},  faceToken:{}", trackId, times, detectedFace.getFaceToken());
			result.setSnapshotRecord(snapshotRecord);
			result.setImage(image);
			result.setFaceToken(detectedFace.getFaceToken());
			result.setConfidence(confidence);
			result.setExpireTime(System.currentTimeMillis() + strategy.getSnapInterval() * 3);
		}
	}

	@Scheduled(fixedRate = 3000)
	public void checkExpire() {
		long start = System.nanoTime();
		for (Entry<Long, TempRecognizedResult> entry : tempRecognizedResults.entrySet()) {
			TempRecognizedResult temp = entry.getValue();
			long expireTime = temp.getExpireTime();
			//long aliveTime = System.currentTimeMillis() - temp.getTimeStamp();
			if (temp != null && System.currentTimeMillis() > expireTime) {
				temp = tempRecognizedResults.remove(entry.getKey());
				// check again in case of anyone else removed it
				if (temp != null) {
					onStranger(temp.camera, temp.getImage(), temp.getSnapshotRecord(), temp.getFaceToken());
				}
			}
		}
		long duration = System.nanoTime() - start;
		logger.trace("@@@ TempRecognizedResults TimeoutChecker finished in {}ns.", duration);
	}

	public class TempRecognizedResult{
		private MonitorImage image;
		private SnapshotRecord snapshotRecord;
		private String faceToken;
		private double confidence;
		private Camera camera;

		private AtomicInteger times = new AtomicInteger();
		private long expireTime;

		public TempRecognizedResult(Camera camera){
			this.camera = camera;
		}

		public MonitorImage getImage() {
			return image;
		}
		public void setImage(MonitorImage image) {
			this.image = image;
		}
		public SnapshotRecord getSnapshotRecord() {
			return snapshotRecord;
		}
		public void setSnapshotRecord(SnapshotRecord snapshotRecord) {
			this.snapshotRecord = snapshotRecord;
		}
		public String getFaceToken() {
			return faceToken;
		}
		public void setFaceToken(String faceToken) {
			this.faceToken = faceToken;
		}
		public double getConfidence() {
			return confidence;
		}
		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}

		public int getTimes(){
			return times.get();
		}

		public int addTimes(){
			return times.incrementAndGet();
		}
		public long getExpireTime() {
			return expireTime;
		}
		public void setExpireTime(long expireTime) {
			this.expireTime = expireTime;
		}
		public Camera getCamera() {
			return camera;
		}
		public void setCamera(Camera camera) {
			this.camera = camera;
		}
	}

}
