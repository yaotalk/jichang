package com.minivision.cameraplat.rest.result.alarm;

public class SnapSearchResult {

    private long id;
    private String snapPicId;
    private String snapPicUrl;
    private double confidence;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSnapPicId() {
        return snapPicId;
    }

    public void setSnapPicId(String snapPicId) {
        this.snapPicId = snapPicId;
    }

    public String getSnapPicUrl() {
        return snapPicUrl;
    }

    public void setSnapPicUrl(String snapPicUrl) {
        this.snapPicUrl = snapPicUrl;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
