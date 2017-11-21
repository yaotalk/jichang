package com.minivision.cameraplat.rest.result.system;

import java.util.List;

public class CameraResult {

    private long id;
    private String deviceName;
    private String deviceIp;
    private String username;
    private String password;
    private int devicePort;
    private int rtspPort;
    private int webPort;
    private Long analyserId;
    private Long regionId;
    private int  type;
    private boolean isOnline ;
    private String videoPlayUrl;

    public String getVideoPlayUrl() {
        return videoPlayUrl;
    }

    public void setVideoPlayUrl(String videoPlayUrl) {
        this.videoPlayUrl = videoPlayUrl;
    }

    private List<Long> entranceGuardIds;
    public CameraResult() {
    }

    public List<Long> getEntranceGuardIds() {
        return entranceGuardIds;
    }

    public void setEntranceGuardIds(List<Long> entranceGuardIds) {
        this.entranceGuardIds = entranceGuardIds;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(int devicePort) {
        this.devicePort = devicePort;
    }

    public int getRtspPort() {
        return rtspPort;
    }

    public void setRtspPort(int rtspPort) {
        this.rtspPort = rtspPort;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public Long getAnalyserId() {
        return analyserId;
    }

    public void setAnalyserId(Long analyserId) {
        this.analyserId = analyserId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
