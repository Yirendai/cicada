package com.yirendai.infra.cicada.cluster;

import com.yirendai.infra.cicada.configure.CicadaWebProps;

import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class ClusterLeaderManager extends LeaderSelectorListenerAdapter {
  private static LeaderSelector leaderSelector;
  private final AtomicBoolean isStop;

  @Autowired
  private CicadaWebProps props;
  @Autowired
  private CuratorFramework zkClient;
  
  public ClusterLeaderManager() {
    super();
    this.isStop = new AtomicBoolean(false);
  }
  
  @PostConstruct
  public void init() {
    leaderSelector = new LeaderSelector(zkClient, props.getMasterNodePath(), this);
    leaderSelector.autoRequeue();
  }

  public static boolean isLeader() {
    return leaderSelector.hasLeadership();
  }

  public void start() {
    leaderSelector.start();
  }

  public void close() {
    leaderSelector.close();
  }

  @Override
  public void takeLeadership(final CuratorFramework client) {
    while (true) {
      if (isStop.get()) {
        isStop.set(false);
        break;
      }

      try {
        TimeUnit.SECONDS.sleep(3);
      } catch (InterruptedException ex) {
        log.warn("interrupted sleep, error:{}", ex);
      }
    }
  }

  @Override
  public void stateChanged(final CuratorFramework client, final ConnectionState state) {
    if (state == ConnectionState.LOST || state == ConnectionState.SUSPENDED) {
      log.warn("connection state changed, now");
      isStop.set(true);
    }
  }
}
