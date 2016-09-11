package com.yirendai.infra.cicada.configure;

import com.yirendai.infra.cicada.util.ZookeeperUtil;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JobCacheConfigure {
  private static final String CICADA_JOBNODE_DEFAULT_CONTENT = "jobNode";

  @Autowired
  private CicadaWebProps props;

  @Autowired
  private CuratorFramework zkClient;

  @Bean
  public NodeCache jobCache() {
    final String jobNodePath = props.getJobNodePath();
    createJobNodeIfNotExists(jobNodePath);
    
    final NodeCache jobCache = new NodeCache(zkClient, jobNodePath, false);
    try {
      jobCache.start();
    } catch (Exception ex) {
      log.error("failed start jobCache on path: {}, error: {}", jobNodePath, ex);
    }

    return jobCache;
  }
  
  @SneakyThrows
  private void createJobNodeIfNotExists(final String nodePath) {
    if (!ZookeeperUtil.exists(zkClient, nodePath)) {
      ZookeeperUtil.create(zkClient, nodePath, CICADA_JOBNODE_DEFAULT_CONTENT);
    }
  }
}
