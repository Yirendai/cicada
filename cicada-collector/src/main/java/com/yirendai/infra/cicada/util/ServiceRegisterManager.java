package com.yirendai.infra.cicada.util;

import com.yirendai.infra.cicada.service.MethodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServiceRegisterManager {
  private final AppMapper mapper = new AppMapper();
  
  @Autowired
  private MethodService methodService;

  public Map<String, Integer> getRegisterInfo(final String appName, // 
      final String serviceName, //
      final String methodName) {
    ServiceMapper serviceMapper = mapper.getServiceMapper(appName);
    if (serviceMapper == null) {
      serviceMapper = new ServiceMapper();
      mapper.putServiceMapper(appName, serviceMapper);
    }

    MethodMapper methodMapper = serviceMapper.getMethodMapper(serviceName);
    if (methodMapper == null) {
      methodMapper = new MethodMapper();
      serviceMapper.putMethodMapper(serviceName, methodMapper);
    }

    Map<String, Integer> idMap = methodMapper.getIdMap(methodName);
    if (idMap == null) {
      idMap = methodService.getMethodIdMap(appName, serviceName, methodName);
      methodMapper.putIdMap(methodName, idMap);
    }

    return idMap;
  }
  
  private class MethodMapper {
    final Map<String, Map<String, Integer>> infoMap = new ConcurrentHashMap<String, Map<String, Integer>>();

    final Map<String, Integer> getIdMap(final String key) {
      return infoMap.get(key);
    }

    void putIdMap(final String key, final Map<String, Integer> idMap) {
      infoMap.put(key, idMap);
    }
  }

  private class ServiceMapper {
    Map<String, MethodMapper> map = new ConcurrentHashMap<String, MethodMapper>();

    MethodMapper getMethodMapper(final String key) {
      return map.get(key);
    }

    void putMethodMapper(final String key, final MethodMapper mapper) {
      map.put(key, mapper);
    }
  }

  private class AppMapper {
    Map<String, ServiceMapper> map = new ConcurrentHashMap<String, ServiceMapper>();

    ServiceMapper getServiceMapper(final String key) {
      return map.get(key);
    }

    void putServiceMapper(final String key, final ServiceMapper mapper) {
      map.put(key, mapper);
    }
  }
}
