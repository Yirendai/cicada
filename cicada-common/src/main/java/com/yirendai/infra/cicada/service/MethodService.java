package com.yirendai.infra.cicada.service;

import com.yirendai.infra.cicada.entity.AppInfo;
import com.yirendai.infra.cicada.entity.MethodInfo;
import com.yirendai.infra.cicada.entity.ServiceInfo;
import com.yirendai.infra.cicada.repository.AppRepository;
import com.yirendai.infra.cicada.repository.MethodRepository;
import com.yirendai.infra.cicada.repository.ServiceRepository;
import com.yirendai.infra.cicada.resource.RegisterInfoResource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.transaction.Transactional;

@Slf4j
@Component
@Transactional
public class MethodService {
  @Autowired
  ServiceService serviceService;

  @Autowired
  MethodRepository methodRepo;

  @Autowired
  ServiceRepository serviceRepo;

  @Autowired
  AppRepository appRepo;

  public int getMethodId(final String appName, final String serviceName, final String methodName) {
    final int serviceId = serviceService.getServiceId(appName, serviceName);
    MethodInfo methodInfo = methodRepo.findByServiceIdAndMethodName(serviceId, methodName);
    if (methodInfo == null) {
      methodInfo = new MethodInfo();
      methodInfo.setMethodName(methodName);
      methodInfo.setServiceId(serviceId);
      methodRepo.save(methodInfo);
    }

    return methodInfo.getId();
  }

  public Map<String, Integer> getMethodIdMap(final String appName, // 
      final String serviceName, // 
      final String methodName) {
    final Map<String, Integer> map = serviceService.getServiceIdMap(appName, serviceName);
    final int serviceId = map.get("serviceId");
    MethodInfo methodInfo = methodRepo.findByServiceIdAndMethodName(serviceId, methodName);
    if (methodInfo == null) {
      methodInfo = new MethodInfo();
      methodInfo.setMethodName(methodName);
      methodInfo.setServiceId(serviceId);
      methodRepo.save(methodInfo);
    }

    map.put("methodId", methodInfo.getId());
    return map;
  }

  public RegisterInfoResource getRegisterInfo(final int methodId) {

    final RegisterInfoResource res;
    final MethodInfo methodInfo = methodRepo.findOne(methodId);
    if (methodInfo == null) {
      log.warn("failed get method info, method id: {}", methodId);
      res = null;
    } else {

      final ServiceInfo serviceInfo = serviceRepo.findOne(methodInfo.getServiceId());
      final AppInfo appInfo = appRepo.findOne(serviceInfo.getAppId());

      res = new RegisterInfoResource();
      res.setAppName(appInfo.getAppName());
      res.setServiceName(serviceInfo.getServiceName());
      res.setMethodName(methodInfo.getMethodName());
    }

    return res;
  }

  public Page<MethodInfo> getMethodsByService(final int serviceId, final Pageable pageable) {
    return methodRepo.findByServiceId(serviceId, pageable);
  }

  public Page<MethodInfo> findByServiceIdAndMethodNameLike(final int serviceId, // 
      final String methodName, //
      final Pageable pageable) {
    
    final Page<MethodInfo> pageResult;
    if (StringUtils.isEmpty(methodName)) {
      pageResult = methodRepo.findByServiceId(serviceId, pageable);
    } else {
      final String fuzzyMethodName = new StringBuilder().append("%").append(methodName).append("%").toString();
      pageResult = methodRepo.findByServiceIdAndMethodNameLike(serviceId, fuzzyMethodName, pageable);
    }

    return pageResult;
  }

  public Page<MethodInfo> findByMethodNameLike(final String methodName, final Pageable pageable) {
    return methodRepo.findByMethodNameLike(methodName, pageable);
  }
}
