package com.yirendai.infra.cicada.service;

import com.yirendai.infra.cicada.entity.AppInfo;
import com.yirendai.infra.cicada.repository.AppRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class AppService {
  @Autowired
  AppRepository repo;

  public int getAppId(final String appName) {
    AppInfo appInfo = repo.findByAppName(appName);
    if (appInfo == null) {
      appInfo = new AppInfo();
      appInfo.setAppName(appName);

      repo.save(appInfo);
    }

    return appInfo.getId();
  }

  public Page<AppInfo> fetchAppInfos(final Pageable pageable) {
    return repo.findAll(pageable);
  }

  public Page<AppInfo> findByAppNameLike(final String appName, final Pageable pageable) {
    final Page<AppInfo> appInfoPage;
    if (StringUtils.isEmpty(appName)) {
      appInfoPage = repo.findAll(pageable);
    } else {
      final String fuzzyAppName = new StringBuilder().append("%").append(appName).append("%").toString();
      appInfoPage = repo.findByAppNameLike(fuzzyAppName, pageable);
    }
    
    return appInfoPage;
  }
}
