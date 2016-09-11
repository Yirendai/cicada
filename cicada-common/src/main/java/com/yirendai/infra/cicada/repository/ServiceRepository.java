package com.yirendai.infra.cicada.repository;

import com.yirendai.infra.cicada.entity.ServiceInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

@Transactional
public interface ServiceRepository extends JpaRepository<ServiceInfo, Integer> {

  @Query 
  ServiceInfo findByAppIdAndServiceName(int appId, String serviceName);

  @Query 
  Page<ServiceInfo> findByAppId(int appId,Pageable pageable);

  @Query 
  Page<ServiceInfo> findByAppIdAndServiceNameLike(int appId, String serviceName,Pageable pageable);

  @Query 
  Page<ServiceInfo> findByServiceNameLike(String serviceName,Pageable pageable);
}
