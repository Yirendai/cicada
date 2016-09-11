package com.yirendai.infra.cicada.service;

import com.yirendai.infra.cicada.entity.model.AnnotationModel;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.repository.TraceElasticRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElasticUploadService {
  private static final String SPAN_TYPE_STR = "span";
  private static final String ANNOTATION_TYPE_STR = "annotation";

  @Autowired
  private TraceElasticRepository repo;

  public void upload(final List<SpanModel> spans, final List<AnnotationModel> annos) {
    repo.upload(SPAN_TYPE_STR, spans);
    repo.upload(ANNOTATION_TYPE_STR, annos);
  }
}
