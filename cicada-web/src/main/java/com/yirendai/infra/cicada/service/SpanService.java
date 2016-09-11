package com.yirendai.infra.cicada.service;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.repository.SpanRepository;
import com.yirendai.infra.cicada.request.EntityPageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Loggable
public class SpanService {
  @Autowired
  SpanRepository repo;

  @Autowired
  AppManagerService appManager;

  public List<SpanModel> fetchSpanPage(final EntityPageRequest request) {
    return repo.fetchSpanModelPage(request);
  }

  public List<SpanModel> fetchErrorSpanPage(final EntityPageRequest request) {
    return repo.fetchErrorSpanModelPage(request);
  }
}
