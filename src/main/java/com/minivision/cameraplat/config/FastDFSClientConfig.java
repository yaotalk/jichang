package com.minivision.cameraplat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import fastdfs.client.FastdfsClient;

/**
 * 文件存储需要使用到的Bean配置
 * @author hughzhao
 * @2017年7月28日
 */
@Configuration
public class FastDFSClientConfig {

	@Autowired
	FastDFSConfig fastdfsConfig;
	
	/**
	 * FastDFS客户端Bean
	 * @return
	 */
	@Bean
	public FastdfsClient fastdfsClient() {
		FastdfsClient.Builder clientBuilder = FastdfsClient.newBuilder();
		if (fastdfsConfig.getConnectTimeout() > 0) {
			clientBuilder.connectTimeout(fastdfsConfig.getConnectTimeout());
		}
		if (fastdfsConfig.getReadTimeout() > 0) {
			clientBuilder.readTimeout(fastdfsConfig.getReadTimeout());
		}
		if (fastdfsConfig.getIdleTimeout() > 0) {
			clientBuilder.idleTimeout(fastdfsConfig.getIdleTimeout());
		}
		if (fastdfsConfig.getMaxThreads() > 0) {
			clientBuilder.maxThreads(fastdfsConfig.getMaxThreads());
		}
		if (fastdfsConfig.getMaxConnPerHost() > 0) {
			clientBuilder.maxConnPerHost(fastdfsConfig.getMaxConnPerHost());
		}
		if (!CollectionUtils.isEmpty(fastdfsConfig.getTrackerServers())) {
			for (String tracker : fastdfsConfig.getTrackerServers()) {
				String[] ipPort = tracker.split(":");
				clientBuilder.tracker(ipPort[0], Integer.parseInt(ipPort[1]));
			}
		}
		// FastdfsClient is threadsafe and use connection pool
		return clientBuilder.build();
	}
	
}
