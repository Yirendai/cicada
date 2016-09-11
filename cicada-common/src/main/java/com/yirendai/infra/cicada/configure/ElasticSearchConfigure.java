package com.yirendai.infra.cicada.configure;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Configuration
public class ElasticSearchConfigure {
  @Value("${elasticsearch.node.addr}")
  private String nodeAddr;

  @Value("${elasticsearch.node.port}")
  private int nodePort;

  @Value("${elasticsearch.cluster.name}")
  private String clusterName;

  @Bean
  public TransportClient client() {
    TransportClient client = null;
    try {
      client = TransportClient.builder().settings(this.settings()).build() //
          .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(nodeAddr), nodePort));
    } catch (UnknownHostException ex) {
      log.error("init es client host:{},port:{} error", nodeAddr, nodePort, ex);
    }
    return client;
  }

  @SneakyThrows
  public Settings settings() {
    return Settings.settingsBuilder() //
        .put("cluster.name", clusterName) //
        .put("client.transport.sniff", true) //
        .build();
  }
}
