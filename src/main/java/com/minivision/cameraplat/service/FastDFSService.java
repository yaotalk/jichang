package com.minivision.cameraplat.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minivision.cameraplat.config.FastDFSConfig;

import fastdfs.client.FastdfsClient;
import fastdfs.client.FileId;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FastDFSService {

	@Autowired
	private FastdfsClient fastdfsClient;

	@Autowired
	private FastDFSConfig fastdfsConfig;
	
	public String uploadToFastDFS(String fileName, byte[] fileData) throws InterruptedException, ExecutionException {
		CompletableFuture<FileId> resultFuture = fastdfsClient.upload(fileName, fileData);
		FileId fileId = resultFuture.get();
		return fileId.toBase64String();
	}
	
	public byte[] downloadFromFastDFS(String fileIdBase64) throws InterruptedException, ExecutionException {
		FileId fileId = FileId.fromBase64String(fileIdBase64);
		return fastdfsClient.download(fileId).get();
	}
	
	public String getFileUrl(String fileIdBase64) {
		FileId fileId = FileId.fromBase64String(fileIdBase64);
		String group = fileId.group();
		String path = fileId.path();
		String urlPrefix = fastdfsConfig.getUrlPrefix().get(group);
		String fileUrl = urlPrefix + path;
		log.trace("文件地址：" + fileUrl);
		return fileUrl;
	}
	
}
