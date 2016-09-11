package com.yirendai.infra.cicada.util;

import com.yirendai.infra.cicada.config.CicadaCollectorProps;
import com.yirendai.infra.cicada.entity.model.AnnotationModel;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.entity.trace.Annotation;
import com.yirendai.infra.cicada.entity.trace.BinaryAnnotation;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.utils.TraceUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 调用链存储对象生成工具 针对日志读取的对象进行清洗，生成Trace/Span/Annotation的存储对象.
 * 
 * @author Zecheng
 *
 */
@Component
public class TraceChainModelGenerator {
  @Autowired
  private ServiceRegisterManager manager;

  @Autowired
  private CicadaCollectorProps props;

  public SpanModel genSpanModel(final Span span) {
    final SpanModel model;
    final int durationServer = TraceUtil.calcSpanDurationServer(span);
    if (durationServer < 0) {
      model = null;
    } else {
      model = new SpanModel();
      BeanUtils.copyProperties(span, model);

      final Map<String, Integer> idMap =
          manager.getRegisterInfo(span.getAppName(), span.getServiceName(), span.getMethodName());
      model.setAppId(idMap.get("appId"));
      model.setServiceId(idMap.get("serviceId"));
      model.setMethodId(idMap.get("methodId"));
      model.setDurationServer(durationServer);

      final Annotation sr = TraceUtil.getSrAnnotation(span.getAnnotations());
      if (sr != null) {
        model.setStartTime(sr.getTimestamp());
      }
      model.calcSliceNo(props.getJobSlotRange());
    }

    return model;
  }

  public AnnotationModel genAnnotationModel(final Span span, final Annotation anno) {
    final AnnotationModel model = new AnnotationModel();
    BeanUtils.copyProperties(anno, model);

    model.setType(anno.getType().name());
    model.setTraceId(span.getTraceId());
    model.setSpanId(span.getId());

    return model;
  }

  public AnnotationModel genAnnotationModel(final Span span, final BinaryAnnotation binAnno) {
    final AnnotationModel model = new AnnotationModel();
    BeanUtils.copyProperties(binAnno, model);

    model.setType(binAnno.getType().name());
    model.setTraceId(span.getTraceId());
    model.setSpanId(span.getId());

    return model;
  }
}
