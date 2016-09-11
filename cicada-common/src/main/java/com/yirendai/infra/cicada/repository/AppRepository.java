package com.yirendai.infra.cicada.repository;

import com.yirendai.infra.cicada.entity.AppInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppRepository extends JpaRepository<AppInfo, Integer> {

  @Query
  AppInfo findByAppName(String appName);

  @Query
  Page<AppInfo> findByAppNameLike(String appName,Pageable pageable);
}
