package com.yirendai.infra.cicada.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;

@Slf4j
public class ZookeeperUtil {
  private static final int SESSION_TIMEOUT_MILLIS = 5000;
  private static final int CONNECT_TIMEOUT_MILLIS = 3000;

  public static CuratorFramework getClient(final String addr) {

    final RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
    return CuratorFrameworkFactory.builder() //
        .connectString(addr) //
        .sessionTimeoutMs(SESSION_TIMEOUT_MILLIS) // 
        .connectionTimeoutMs(CONNECT_TIMEOUT_MILLIS) //
        .retryPolicy(policy) //
        .build();
  }
  
  public static CuratorFramework getClient(final String addr, final String namespace) {
    final RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
    return CuratorFrameworkFactory.builder() //
        .connectString(addr) //
        .sessionTimeoutMs(SESSION_TIMEOUT_MILLIS) // 
        .connectionTimeoutMs(CONNECT_TIMEOUT_MILLIS) //
        .retryPolicy(policy) //
        .namespace(namespace).build();
  }

  /**
   * check if znode exists.
   */
  @SneakyThrows
  public static boolean exists(final CuratorFramework client, final String path) {
    boolean exists = false;
    try {
      final Stat stat = client.checkExists().forPath(path);
      exists = stat != null;
    } catch (Exception ex) {
      log.error("failed check stat of path: {}, error: {}", path, ex);
      throw ex;
    }

    return exists;
  }

  /**
   * this will create the given ZNode with the given data.
   */
  @SneakyThrows
  public static void create(final CuratorFramework client, final String path, final String content) {
    try {
      if (content == null) {
        client.create().creatingParentsIfNeeded().forPath(path);
      } else {
        client.create().creatingParentsIfNeeded().forPath(path, content.getBytes());
      }
    } catch (NodeExistsException ex) {
      log.warn("node exists, can not create it again!");
    } catch (Exception ex) {
      log.error("failed create znode: {}, error: {}", path, ex);
      throw ex;
    }
  }

  /**
   * this will create the given EPHEMERAL ZNode with the given data.
   */
  @SneakyThrows
  public static void createEphemeral(final CuratorFramework client, final String path, final String content) {
    try {
      if (content == null) {
        client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
      } else {
        client.create().withMode(CreateMode.EPHEMERAL).forPath(path, content.getBytes());
      }
    } catch (NodeExistsException ex) {
      log.warn("node exists, can not create it again!");
    } catch (Exception ex) {
      log.error("failed create EPHEMERAL znode: {}, error: {}", path, ex);
    }
  }
}
