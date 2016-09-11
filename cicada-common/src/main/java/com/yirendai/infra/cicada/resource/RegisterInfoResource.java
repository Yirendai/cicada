package com.yirendai.infra.cicada.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterInfoResource {
  private String appName;
  private String serviceName;
  private String methodName;
}
