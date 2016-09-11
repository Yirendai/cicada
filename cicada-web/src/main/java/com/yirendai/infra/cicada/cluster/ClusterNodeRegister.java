package com.yirendai.infra.cicada.cluster;

import com.yirendai.infra.cicada.configure.CicadaWebProps;

import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ClusterNodeRegister {
  private static final String NODE_LABEL = UUID.randomUUID().toString();
  private static final String PATH_SEPERATOR = "/" ;

  @Autowired
  private CicadaWebProps props;

  @Autowired
  private CuratorFramework zkClient;

  public static String getNodeLabel() {
    return NODE_LABEL;
  }

  public void register() {

    final String nodePath = buildNodePath();

    try {
      zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath);
    } catch (Exception ex) {
      log.error("failed create znode: {}, error: {}", nodePath, ex);
      // System.exit(-1);
    }
  }

  private String buildNodePath() {
    final StringBuilder sb = new StringBuilder();
    sb.append(props.getInstancesNodePath()).append(PATH_SEPERATOR).append(NODE_LABEL);
    return sb.toString();
  }
}
