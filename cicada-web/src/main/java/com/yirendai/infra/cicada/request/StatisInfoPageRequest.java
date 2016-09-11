package com.yirendai.infra.cicada.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.joda.time.DateTime;

@Data
@AllArgsConstructor
public class StatisInfoPageRequest {

  private Integer methodId;
  @ApiModelProperty(name = "beginTime", value = "ISO8601 time format", dataType = "string")
  private DateTime beginTime;

  @ApiModelProperty(name = "endTime", value = "ISO8601 time format", dataType = "string")
  private DateTime endTime;

  int page;
  int size;
  
  public StatisInfoPageRequest() {
    this.endTime = DateTime.now();
    this.page = 0;
    this.size = 20;
  }

  @JsonIgnore
  public boolean isValid() {
    final boolean valid;
    if (methodId == null || beginTime == null) {
      valid = false;
    } else {
      valid = page >= 0 && size > 0;
    }

    return valid;
  }
}
