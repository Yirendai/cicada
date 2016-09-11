package com.yirendai.infra.cicada.repository;

import com.yirendai.infra.cicada.entity.MethodInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

@Transactional
public interface MethodRepository extends JpaRepository<MethodInfo, Integer> {

  @Query
  MethodInfo findByServiceIdAndMethodName(int serviceId, String methodName);

  @Query
  Page<MethodInfo> findByServiceId(int serviceId,Pageable pageable);

  @Query 
  Page<MethodInfo> findByServiceIdAndMethodNameLike(int serviceId,String methodName,Pageable pageable);

  @Query
  Page<MethodInfo> findByMethodNameLike(String methodName,Pageable pageable);
}
