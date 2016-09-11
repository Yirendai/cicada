package com.yirendai.infra.cicada.entity;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Job {
  private long id;
  private long startTimestamp;
  private long endTimestamp;
  private Map<String, Fragment> infos;
  
  public Job() {
    this.infos  = new HashMap<String, Fragment>();
  }

  public void addJobFragment(final String key, final Fragment fragment) {
    infos.put(key, fragment);
  }

  public Fragment getJobFragment(final String key) {
    return infos.get(key);
  }

  public void incrJodId() {
    ++this.id;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
