package com.yirendai.infra.cicada.entity.trace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint implements Serializable {
  private static final long serialVersionUID = -1819879293130044091L;
  private String ip;
  private int port;

  @Override
  public String toString() {
    return "Endpoint{" + "ip='" + ip + '\'' + ", port=" + port + '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Endpoint)) {
      return false;
    }

    Endpoint endpoint = (Endpoint) obj;
    if (!ip.equals(endpoint.ip)) {
      return false;
    }
    if (port != endpoint.port) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (ip != null ? ip.hashCode() : 0);
    result = 31 * result + port;
    return result;
  }
}
