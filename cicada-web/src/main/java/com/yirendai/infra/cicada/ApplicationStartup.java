package com.yirendai.infra.cicada;

import com.yirendai.infra.cicada.cluster.ClusterJobMonitor;
import com.yirendai.infra.cicada.cluster.ClusterLeaderManager;
import com.yirendai.infra.cicada.cluster.ClusterNodeRegister;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
  @Autowired
  private ClusterJobMonitor jobListener;

  @Autowired
  private ClusterNodeRegister nodeRegister;

  @Autowired
  private ClusterLeaderManager leaderManager;

  private void start() {
    // 首先监听JOB
    jobListener.start();

    // 注册node节点
    nodeRegister.register();

    // 执行选主逻辑
    leaderManager.start();
  }

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    start();
  }
}
