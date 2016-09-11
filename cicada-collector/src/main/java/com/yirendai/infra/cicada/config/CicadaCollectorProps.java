package com.yirendai.infra.cicada.config;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CicadaCollectorProps {
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
  
  @Value("${job.slot.range}")
  private int jobSlotRange;

  @Value("${trace.progress.file.path}")
  private String progressFilePath;

  @Value("${trace.log.root.dir}")
  private String traceLogRootDir;
}
