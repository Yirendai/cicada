package com.yirendai.infra.cicada.entity.trace;

import com.alibaba.fastjson.annotation.JSONField;
import com.yirendai.infra.cicada.constants.AnnotationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annotation implements Serializable {
  private static final long serialVersionUID = 5906564687914191864L;

  private long timestamp;
  private AnnotationType type;
  private String ip;
  private int port;
  private int duration; // 这是个没有意义的字段，主要用于清洗成AnnotationModel时可以getDuration

  public void setEndpoint(Endpoint endpoint) {
    if (endpoint == null) {
      return;
    }

    this.ip = endpoint.getIp();
    this.port = endpoint.getPort();
  }

  @JSONField(serialize = false)
  public Endpoint getEndpoint() {
    Endpoint endpoint = new Endpoint();
    endpoint.setIp(ip);
    endpoint.setPort(port);
    return endpoint;
  }

  private boolean equals(String str1, String str2) {
    return (str1 != null ? str1.equals(str2) : str2 == null);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Annotation)) {
      return false;
    }

    Annotation that = (Annotation) obj;
    if (timestamp != that.timestamp) {
      return false;
    }

    if (type != that.type) {
      return false;
    }

    if (!this.equals(ip, that.ip)) {
      return false;
    }

    if (port != that.port) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (ip != null ? ip.hashCode() : 0);
    result = 31 * result + port;
    return result;
  }

  @Override
  public String toString() {
    return "Annotation{" + "timestamp=" + timestamp + ", type='" + type + '\'' + ", ip=" + ip + ", port=" + port + '}';
  }

}
