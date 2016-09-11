package com.yirendai.infra.cicada.entity;

import com.alibaba.fastjson.JSON;

import lombok.Data;

@Data
public class JobSlice {
  private long startTimestamp;
  private long endTimestamp;
  private int start;
  private int end;

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
