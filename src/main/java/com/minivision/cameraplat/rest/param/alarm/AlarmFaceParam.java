package com.minivision.cameraplat.rest.param.alarm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;

import com.minivision.cameraplat.rest.param.RestParam;

public class AlarmFaceParam extends RestParam{

     private static final long serialVersionUID = 181434001367270631L;

     @ApiModelProperty(value = "日志ID",required = true)
     private String logid;

     @ApiModelProperty(value = "记录总数")
     @Max(100)
     private int limit = 10;

     public String getLogid() {
          return logid;
     }

     public void setLogid(String logid) {
          this.logid = logid;
     }

     public int getLimit() {
          return limit;
     }

     public void setLimit(int limit) {
          this.limit = limit;
     }

}
