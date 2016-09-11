package com.yirendai.infra.cicada.configure;

import com.yirendai.infra.cicada.util.ZookeeperUtil;

import lombok.SneakyThrows;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuratorZookeeperConfigure {
  private static final char PATH_SEPARATOR = '/';
  private static final String CICADA_NAMESPACE_NODE_CONTENT = "cicada namespace";
  
  @Autowired
  private CicadaWebProps props;
 
  @Bean
  @SneakyThrows
  public CuratorFramework zkClient() {
    this.createNamespaceIfNotExists();
    final CuratorFramework zkClient = ZookeeperUtil.getClient(props.getZkAddr(), props.getZkNamespase());
    zkClient.start();
    return zkClient;
  }

  /**
   * 如果zk中cicada的namespace不存在，则创建.
   */

  @SneakyThrows
  private void createNamespaceIfNotExists() {
    final CuratorFramework client = ZookeeperUtil.getClient(props.getZkAddr());
        
    String namespace = props.getZkNamespase();
    if (namespace.charAt(0) != PATH_SEPARATOR) {
      final StringBuilder sb = new StringBuilder();
      sb.append(PATH_SEPARATOR).append(namespace);
      namespace = sb.toString();
    }

    client.start();
    try {
      if (!ZookeeperUtil.exists(client, namespace)) {
        ZookeeperUtil.create(client, namespace, CICADA_NAMESPACE_NODE_CONTENT);
      }
    } finally {
      client.close();
    }
  }
}
