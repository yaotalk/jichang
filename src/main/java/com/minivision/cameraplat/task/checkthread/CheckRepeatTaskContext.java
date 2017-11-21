package com.minivision.cameraplat.task.checkthread;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.minivision.cameraplat.task.BatchTask;
import com.minivision.cameraplat.task.CheckRepeatTask;
import com.minivision.cameraplat.task.MD5InitTask;

//只允许单任务
@Component
public class CheckRepeatTaskContext {

	@Autowired
	private MD5InitTask md5InitTask;

	@Autowired
	private CheckRepeatTask checkRepeatTask;
	
	private String md5InitTaskId;
	
	private String statusText;
	
	private String faceSetName;
	
	boolean dataIniting;
	
	@Autowired
	private SimpMessagingTemplate messageTemplate;

	@Async
	public void startDeal(String faceSetToken) {
		md5InitTask.setFaceSetToken(faceSetToken);
		md5InitTask.setInitingMD5(true);
		md5InitTask.setTaskStatus(BatchTask.PREPARED);
		md5InitTask.setTaskId(md5InitTaskId);
		md5InitTask.setCheckRepeatTaskContext(this);
		Thread md5InitThread = new Thread(md5InitTask);
		md5InitThread.start();
	}
	
	@Async
	public void startCheckRepeat(String faceSetToken) {
		checkRepeatTask.setTaskId(md5InitTaskId);
		checkRepeatTask.setFaceSetToken(faceSetToken);
		checkRepeatTask.setContinued(true);
		checkRepeatTask.setTaskStatus(BatchTask.PREPARED);
		checkRepeatTask.setCheckRepeatTaskContext(this);
		checkRepeatTask.check();
		md5InitTask.setInitingMD5(false);
		dataIniting=false;
		Map<String,Object> res = new HashMap<String,Object>();
		res.put("dataIniting", isDataIniting());
		sendStatus(md5InitTask.getTaskId(),res);
	}

	public boolean isDataIniting() {
		return dataIniting;
	}

	public void setDataIniting(boolean dataIniting) {
		this.dataIniting = dataIniting;
	}

	public String getFaceSetName() {
		return faceSetName;
	}

	public void setFaceSetName(String faceSetName) {
		this.faceSetName = faceSetName;
	}
	
	public void sendStatus(String taskId, Object obj){
		messageTemplate.convertAndSend("/w/task/"+taskId, obj);
	}
	
	public void stopInitalData(){
		dataIniting=false;
		md5InitTask.setInitingMD5(false);
		md5InitTask.setContinued(false);
		checkRepeatTask.stopTask();
	}

	public String getMd5InitTaskId() {
		return md5InitTaskId;
	}

	public void setMd5InitTaskId(String md5InitTaskId) {
		this.md5InitTaskId = md5InitTaskId;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
}
