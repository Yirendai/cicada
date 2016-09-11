package com.yirendai.infra.cicada.entity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 清洗后的Annotation和BinaryAnnotation数据存储模型.
 * @author Zecheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationModel {
  private String traceId;
  private String spanId;

  private String key;
  private String value;
  private String type;
  private long timestamp;
  private int duration;
  private String ip;
  private int port;
}
