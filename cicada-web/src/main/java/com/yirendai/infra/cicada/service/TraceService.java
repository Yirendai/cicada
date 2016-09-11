package com.yirendai.infra.cicada.service;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.constants.AnnotationType;
import com.yirendai.infra.cicada.entity.SpanEntity;
import com.yirendai.infra.cicada.entity.model.AnnotationModel;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.repository.AnnotationRepository;
import com.yirendai.infra.cicada.repository.SpanRepository;
import com.yirendai.infra.cicada.resource.RegisterInfoResource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

@Component
@Loggable
public class TraceService {
  // private static final Logger LOG = LoggerFactory.getLogger(TraceService.class);

  @Autowired
  SpanRepository spanRepo;

  @Autowired
  AnnotationRepository annoRepo;

  @Autowired
  AppManagerService appManager;

  /**
   * <p>
   * 计算spanEntity的durationClient 并将cs cr 从返回的AnnotationModel列表中删除 .
   * 对annotationModel列表中的时间按照时间戳排序.
   * </p>
   */
  private final Set<String> excludeAnnotations;

  public TraceService() {
    excludeAnnotations = new HashSet<String>();
    excludeAnnotations.add(AnnotationType.CLIENT_SEND.name());
    excludeAnnotations.add(AnnotationType.SERVER_SEND.name());
    excludeAnnotations.add(AnnotationType.CLIENT_RECEIVE.name());
    excludeAnnotations.add(AnnotationType.SERVER_RECEIVE.name());
  }

  /**
   * 根据traceId获取整个调用链的信息.
   */
  public List<SpanEntity> fetchTraceChain(final String traceId) {
    final List<SpanEntity> results = new LinkedList<SpanEntity>();

    final List<SpanModel> spanModels = spanRepo.getTraceSpanModels(traceId);
    for (final SpanModel model : spanModels) {
      // 根据TraceId和SpanId获取annotation链
      final List<AnnotationModel> annoModels = annoRepo.getSpanAnnotations(traceId, model.getId());
      final RegisterInfoResource registerInfo = appManager.getRegisterInfo(model.getMethodId());

      
      results.add(genSpanModel(model, registerInfo, annoModels));
    }

    // 排序
    Collections.sort(results);
    return results;
  }

  private SpanEntity genSpanModel(final SpanModel model, final RegisterInfoResource registerInfo,
      final List<AnnotationModel> annoModels) {
    final SpanEntity entity = new SpanEntity();
    BeanUtils.copyProperties(model, entity);
    entity.setAppName(registerInfo.getAppName());
    entity.setServiceName(registerInfo.getServiceName());
    entity.setMethodName(registerInfo.getMethodName());
    entity.setAnnotations(annoModels);

    wash(entity);
    return entity;
  }

  private void wash(final SpanEntity span) {
    long cr = -1;
    long cs = -1;
    final ListIterator<AnnotationModel> iter = span.getAnnotations().listIterator();
    while (iter.hasNext()) {
      final AnnotationModel anno = iter.next();
      final String annoType = anno.getType();
      if (excludeAnnotations.contains(annoType)) {
        if (AnnotationType.CLIENT_SEND.name().equals(annoType)) {
          cs = anno.getTimestamp();
        }
        if (AnnotationType.CLIENT_RECEIVE.name().equals(annoType)) {
          cr = anno.getTimestamp();
        }

        iter.remove();
      }
    }

    if (cr != -1 && cs != -1) {
      span.setDurationClient((int) (cr - cs));
    }

    Collections.sort(span.getAnnotations(), new Comparator<AnnotationModel>() {
      public int compare(final AnnotationModel anno1, final AnnotationModel anno2) {
        return (int) (anno1.getTimestamp() - anno2.getTimestamp());
      }
    });
  }
}
