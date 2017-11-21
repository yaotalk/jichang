package com.minivision.cameraplat.rest.param.alarm;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.Max;

import com.minivision.cameraplat.rest.param.RestParam;

public class SnapShotParam extends RestParam {

    private static final long serialVersionUID = -534673110955382073L;

    private long startTime;

    private long endTime ;

    private String sex;

    @ApiParam(required = true)
    private long cameraId;

    private int offset = 0;

    @Max(100)
    private int limit = 10;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getCameraId() {
        return cameraId;
    }

    public void setCameraId(long cameraId) {
        this.cameraId = cameraId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
