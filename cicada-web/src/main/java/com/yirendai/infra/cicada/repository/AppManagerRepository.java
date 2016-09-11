package com.yirendai.infra.cicada.repository;

import com.yirendai.infra.cicada.resource.RegisterInfoResource;
import com.yirendai.infra.cicada.service.MethodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AppManagerRepository {
  @Autowired
  private MethodService methodService;

  public Map<String, Integer> register(final String appName, //
      final String serviceName, //
      final String methodName) {
    return methodService.getMethodIdMap(appName, serviceName, methodName);
  }

  public RegisterInfoResource getRegisterInfo(final int methodId) {
    return methodService.getRegisterInfo(methodId);
  }
}
