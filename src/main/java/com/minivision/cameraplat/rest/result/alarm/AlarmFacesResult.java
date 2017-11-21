package com.minivision.cameraplat.rest.result.alarm;
public class AlarmFacesResult {

     private String id;
     private String name;
     private double confidence;
     private String userImgUrl;
     private String employeeId;
    public AlarmFacesResult() {
    }

    public AlarmFacesResult(String id, String name, int confidence, String userImgUrl,String employeeId) {
        this.id = id;
        this.name = name;
        this.confidence = confidence;
        this.userImgUrl = userImgUrl;
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }
}
