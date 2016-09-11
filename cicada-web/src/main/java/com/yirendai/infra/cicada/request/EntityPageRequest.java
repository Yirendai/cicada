package com.yirendai.infra.cicada.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.joda.time.DateTime;

/**
 * 业务方法调用查询请求.
 * @author Zecheng
 */
@Data
@AllArgsConstructor
public class EntityPageRequest {

  private Integer methodId;

  @ApiModelProperty(name = "beginTime", value = "ISO8601 time format", dataType = "string")
  private DateTime beginTime;

  @ApiModelProperty(name = "endTime", value = "ISO8601 time format", dataType = "string")
  private DateTime endTime;

  @ApiModelProperty(value = "区间查询条件：处理时间下限，默认为0")
  private int floorDuration;

  @ApiModelProperty(value = "区间查询条件：处理时间上限，默认100s")
  private int ceilDuration;

  private int page;
  private int size;

  public EntityPageRequest() {
    endTime = DateTime.now();
    floorDuration = 0;
    ceilDuration = 100 * 1000;
    page = 0;
    size = 20;
  }

  @JsonIgnore
  public boolean isValid() {
    boolean valid = true;
    if (methodId == null || beginTime == null || ceilDuration <= 0) {
      valid = false;
    }

    return valid;
  }
}
