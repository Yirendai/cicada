package com.yirendai.infra.cicada.service;

import com.yirendai.infra.cicada.entity.ServiceInfo;
import com.yirendai.infra.cicada.repository.ServiceRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServiceService {

  @Autowired
  private AppService appService;

  @Autowired
  private ServiceRepository serviceRepo;

  public int getServiceId(final String appName, final String serviceName) {
    final int appId = appService.getAppId(appName);
    ServiceInfo service = serviceRepo.findByAppIdAndServiceName(appId, serviceName);
    if (service == null) {
      service = new ServiceInfo();
      service.setAppId(appId);
      service.setServiceName(serviceName);
      serviceRepo.save(service);
    }

    return service.getId();
  }

  public Map<String, Integer> getServiceIdMap(final String appName, final String serviceName) {
    final Map<String, Integer> map = new HashMap<String, Integer>();
    final int appId = appService.getAppId(appName);
    map.put("appId", appId);

    ServiceInfo service = serviceRepo.findByAppIdAndServiceName(appId, serviceName);
    if (service == null) {
      service = new ServiceInfo();
      service.setAppId(appId);
      service.setServiceName(serviceName);
      serviceRepo.save(service);
    }

    map.put("serviceId", service.getId());
    return map;
  }

  public ServiceInfo getServiceInfo(final int serviceId) {
    return serviceRepo.findOne(serviceId);
  }

  public Page<ServiceInfo> getServicesByAppId(final int appId, final Pageable pageable) {
    return serviceRepo.findByAppId(appId, pageable);
  }

  public Page<ServiceInfo> findByAppIdAndserviceNameLike(final int appId, //
      final String serviceName, //
      final Pageable pageable) {
    final Page<ServiceInfo> pageResult;
    if (StringUtils.isEmpty(serviceName)) {
      pageResult = serviceRepo.findByAppId(appId, pageable);
    } else {
      final String fuzzyServiceName = new StringBuilder().append("%").append(serviceName).append("%").toString();
      pageResult = serviceRepo.findByAppIdAndServiceNameLike(appId, fuzzyServiceName, pageable);
    }

    return pageResult;
  }

  public Page<ServiceInfo> findByServiceNameLike(final String serviceName, final Pageable pageable) {
    return serviceRepo.findByServiceNameLike(serviceName, pageable);
  }
}
