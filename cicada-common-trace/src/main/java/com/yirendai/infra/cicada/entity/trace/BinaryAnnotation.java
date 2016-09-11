package com.yirendai.infra.cicada.entity.trace;

import com.yirendai.infra.cicada.constants.BinaryAnnotationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinaryAnnotation implements Serializable {
  private static final long serialVersionUID = -5829069208358509837L;

  private String key;
  private String value;
  private BinaryAnnotationType type = BinaryAnnotationType.EVENT;
  private long timestamp = System.currentTimeMillis();
  private String ip;
  private int port;
  private int duration = 0;

  public void setThrowable(String className, String methodName, Throwable ex) {
    if (ex != null) {
      ex.printStackTrace();
      setKey(className);
      setValue(methodName + "," + ex.toString());
      setType(BinaryAnnotationType.EXCEPTION);
    }
  }

  @Override
  public String toString() {
    return "BinaryAnnotation{" + "key='" + key + '\'' + ", value=" + value + ", type='" + type + '\'' + ", timestamp="
        + timestamp + ", ip=" + ip + ", port=" + port + '}';
  }

  public void setEndpoint(Endpoint endpoint) {
    if (endpoint == null) {
      return;
    }

    this.ip = endpoint.getIp();
    this.port = endpoint.getPort();
  }
}
