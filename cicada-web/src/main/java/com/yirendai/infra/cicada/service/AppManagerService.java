package com.yirendai.infra.cicada.service;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.repository.AppManagerRepository;
import com.yirendai.infra.cicada.resource.RegisterInfoResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Loggable
@Component
public class AppManagerService {
  private final AppMapper appMapper;
  private final RegisterInfoMapper registerInfoMapper;

  @Autowired
  private AppManagerRepository appManagerRepo;
  
  public AppManagerService() {
    this.appMapper = new AppMapper();
    this.registerInfoMapper = new RegisterInfoMapper();
  }

  public Map<String, Integer> register(final String appName, final String serviceName, final String methodName) {
    ServiceMapper serviceMapper = appMapper.getServiceMapper(appName);
    if (serviceMapper == null) {
      serviceMapper = new ServiceMapper();
      appMapper.putServiceMapper(appName, serviceMapper);
    }

    MethodMapper methodMapper = serviceMapper.getMethodMapper(serviceName);
    if (methodMapper == null) {
      methodMapper = new MethodMapper();
      serviceMapper.putMethodMapper(serviceName, methodMapper);
    }

    Map<String, Integer> idMap = methodMapper.getIdMap(methodName);
    if (idMap == null) {
      idMap = appManagerRepo.register(appName, serviceName, methodName);
      addRegisterInfo(idMap.get("methodId"), appName, serviceName, methodName);
      methodMapper.putIdMap(methodName, idMap);
    }

    return idMap;
  }

  public RegisterInfoResource getRegisterInfo(final int methodId) {
    RegisterInfoResource res = registerInfoMapper.get(methodId);
    if (res == null) {
      res = appManagerRepo.getRegisterInfo(methodId);
    }
    return res;
  }

  private void addRegisterInfo(final Integer methodId, //
      final String appName, //
      final String serviceName, //
      final String methodName) {
    final RegisterInfoResource res = new RegisterInfoResource();
    res.setAppName(appName);
    res.setServiceName(serviceName);
    res.setMethodName(methodName);

    registerInfoMapper.put(methodId, res);
  }

  /**
   * 存储id到AppName/ServiceName/MethodName的映射.
   */
  private class RegisterInfoMapper {
    final Map<Integer, RegisterInfoResource> infoMaps = new ConcurrentHashMap<Integer, RegisterInfoResource>();

    RegisterInfoResource get(final Integer key) {
      return infoMaps.get(key);
    }

    void put(final Integer key, final RegisterInfoResource value) {
      infoMaps.put(key, value);
    }
  }

  /**
   * 以下三个类存储 AppName/ServiceName/MethodName到id的映射.
   */
  private class MethodMapper {
    Map<String, Map<String, Integer>> infoMap = new HashMap<String, Map<String, Integer>>();

    Map<String, Integer> getIdMap(final String key) {
      return infoMap.get(key);
    }

    void putIdMap(final String key, final Map<String, Integer> idMap) {
      infoMap.put(key, idMap);
    }
  }

  private class ServiceMapper {
    Map<String, MethodMapper> map = new HashMap<String, MethodMapper>();

    MethodMapper getMethodMapper(final String key) {
      return map.get(key);
    }

    void putMethodMapper(final String key, final MethodMapper mapper) {
      map.put(key, mapper);
    }
  }

  private class AppMapper {
    Map<String, ServiceMapper> map = new HashMap<String, ServiceMapper>();

    ServiceMapper getServiceMapper(final String key) {
      return map.get(key);
    }

    void putServiceMapper(final String key, final ServiceMapper mapper) {
      map.put(key, mapper);
    }
  }
}
