package com.yirendai.infra.cicada.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.joda.time.DateTime;

@Data
@AllArgsConstructor
public class StatisInfoRequest {

  private Integer methodId;
  @ApiModelProperty(name = "beginTime", value = "ISO8601 time format", dataType = "string")
  private DateTime beginTime;

  @ApiModelProperty(name = "endTime", value = "ISO8601 time format", dataType = "string")
  private DateTime endTime;
  
  public StatisInfoRequest() {
    this.endTime = DateTime.now();
  }

  @JsonIgnore
  public boolean isValid() {
    return this.methodId != null && this.beginTime != null;
  }
}
