package com.yirendai.infra.cicada.service;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.entity.SpanStatisInfo;
import com.yirendai.infra.cicada.repository.SpanStatisInfoRepository;
import com.yirendai.infra.cicada.request.StatisInfoPageRequest;
import com.yirendai.infra.cicada.request.StatisInfoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Loggable
public class SpanStatisInfoService {
  @Autowired
  SpanStatisInfoRepository repo;

  @Autowired
  AppManagerService appManager;

  public Page<SpanStatisInfo> fetchPage(final StatisInfoPageRequest request, final Pageable pageable) {
    return repo.fetchPage(request.getMethodId(), request.getBeginTime(), request.getEndTime(), pageable);
  }

  public List<SpanStatisInfo> fetchAllByDuration(final StatisInfoRequest request) {
    return repo.findAllByDuration(request.getMethodId(), request.getBeginTime(), request.getEndTime());
  }
}
