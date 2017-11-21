package com.minivision.cameraplat.task.checkthread;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.minivision.cameraplat.domain.Face;
import com.minivision.cameraplat.service.FaceService;
import com.minivision.cameraplat.service.FastDFSService;
import com.minivision.cameraplat.task.MD5InitTask;

public class InitMD5Thread extends Thread{
	
	private static final Logger logger = LoggerFactory.getLogger(MD5InitTask.class);

	private static AtomicInteger success = new AtomicInteger();
	
	private static AtomicInteger fail = new AtomicInteger();

	private static MD5InitTask md5InitTask;
	
	private static FaceService faceService;
	
	private static FastDFSService fastDFSService;
	
	//private static String basePicPath;
	
	private static List<Face> listFace = new ArrayList<Face>();

	
	private static int totalCount;
				
	private Face face;
	
	public InitMD5Thread(Face face){
		this.face=face;
	}
	
	@Override
	public void run() {
		
	    //对于需要保存的图片，计算md5
		//String fileFullPath = basePicPath + File.separator + face.getImgpath();
	    try {
	    	/*File file = new File(fileFullPath);
			FileInputStream fis = new FileInputStream(file);
			byte[] fileContent = new byte[fis.available()];
			fis.read(fileContent);*/
	    	byte[] fileContent = fastDFSService.downloadFromFastDFS(face.getImgpath());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(fileContent);
			BigInteger bi = new BigInteger(1, md5.digest());
			face.setPicMd5(bi.toString(16));
			//face.setPicSize((int)file.length());
			face.setPicSize(fileContent.length);
			//fis.close();
			synchronized(listFace){
			listFace.add(face);
			}
			success.incrementAndGet();
		} catch (Throwable e) {
			logger.error("计算文件的md5时异常", e);
			fail.incrementAndGet();
		}
	    int totalProcessNum = success.intValue()+fail.intValue();
	    if(totalProcessNum == totalCount){
	    	if(md5InitTask.isContinued()){
		    faceService.saveOnly(listFace);
		    listFace.clear();
			md5InitTask.setProcessedNum(totalProcessNum);
	    	md5InitTask.sendCurrentTask();
	    	md5InitTask.finished();
	    	}
	    }
	    if(totalProcessNum%1000 == 0){
	    	synchronized(listFace){
	    	faceService.saveOnly(listFace);
	    	listFace.clear();
	    	}
		    md5InitTask.setProcessedNum(totalProcessNum);
	    	md5InitTask.sendCurrentTask();
	    }
	}

	public Face getFace() {
		return face;
	}
	
	public static void resetCount(){
		success.set(0);
		fail.set(0);
	}

	
	public void setFace(Face face) {
		this.face = face;
	}

	public static void setFaceService(FaceService faceService) {
		InitMD5Thread.faceService = faceService;
	}

	public static void setFastDFSService(FastDFSService fastDFSService) {
		InitMD5Thread.fastDFSService = fastDFSService;
	}

	/*public static void setBasePicPath(String basePicPath) {
		InitMD5Thread.basePicPath = basePicPath;
	}*/

	public static void setTotalCount(int totalCount) {
		InitMD5Thread.totalCount = totalCount;
	}

	public static MD5InitTask getMd5InitTask() {
		return md5InitTask;
	}

	public static void setMd5InitTask(MD5InitTask md5InitTask) {
		InitMD5Thread.md5InitTask = md5InitTask;
	}
	
}
