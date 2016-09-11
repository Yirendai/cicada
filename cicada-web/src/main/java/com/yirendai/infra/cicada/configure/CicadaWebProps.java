package com.yirendai.infra.cicada.configure;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CicadaWebProps {
  @Value("${elasticsearch.node.addr}")
  private String esNodeAddr;

  @Value("${elasticsearch.node.port}")
  private int esNodePort;
  
  @Value("${elasticsearch.cluster.name}")
  private String esClusterName;

  @Value("${elasticsearch.bulk.await.minutes}")
  private int esBulkAwaitMinutes;

  @Value("${elasticsearch.index.type.name}")
  private String esTypeName;
  
  @Value("${elasticsearch.index.span.prefix}")
  private String esSpanIndexPrefix;

  @Value("${elasticsearch.index.annotation.prefix}")
  private String esAnnotationIndexPrefix;

  @Value("${elasticsearch.index.retention.day}")
  private int esIndexRetentionDays;

  @Value("${statistic.zookeeper.node.master}")
  private String masterNodePath;

  @Value("${statistic.zookeeper.node.instances}")
  private String instancesNodePath;

  @Value("${statistic.zookeeper.node.job}")
  private String jobNodePath;

  @Value("${job.slot.range}")
  private int jobSlotRange;

  @Value("${statistic.zookeeper.connection}")
  private String zkAddr;

  @Value("${statistic.zookeeper.namespace}")
  private String zkNamespase;
}
