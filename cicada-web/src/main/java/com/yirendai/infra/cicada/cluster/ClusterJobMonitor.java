package com.yirendai.infra.cicada.cluster;

import org.apache.curator.framework.recipes.cache.NodeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ClusterJobMonitor {
  @Autowired
  private NodeCache jobCache;

  @Autowired
  private JobProcessor jobProcessor;

  public void start() {
    jobCache.getListenable().addListener(jobProcessor);
  }

  public void close() {
    try {
      jobCache.close();
    } catch (IOException ex) {
      // do not need any process
    }
  }
}
