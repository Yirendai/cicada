package com.yirendai.infra.cicada.capture;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yirendai.infra.cicada.constants.AnnotationType;
import com.yirendai.infra.cicada.entity.trace.Annotation;
import com.yirendai.infra.cicada.entity.trace.BinaryAnnotation;
import com.yirendai.infra.cicada.entity.trace.Endpoint;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.transfer.Transfer;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Tracer {

  // private static final Logger logger =
  // LoggerFactory.getLogger(Tracer.class);

  private static Tracer instance = new Tracer();

  private final Sampler sampler = new CustomSampler();

  private Transfer transfer;

  // 传递parentSpan
  private final ThreadLocal<Span> spanThreadLocal = new ThreadLocal<Span>();

  private final ThreadLocal<Span> ctxTheadLocal = new ThreadLocal<Span>();

  private Tracer() {}

  public static Tracer getInstance() {
    return instance;
  }

  void removeParentSpan() {
    spanThreadLocal.remove();
  }

  Span getParentSpan() {
    return spanThreadLocal.get();
  }

  void setParentSpan(final Span span) {
    spanThreadLocal.set(span);
  }

  // 构件Span，参数通过上游接口传递过来
  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  Span genSpan(final String appName, final String serviceName, final String methodName, final String traceId,
      final String pid, final String id, final boolean sample) {
    final Span span = new Span();
    span.setAppName(appName);
    span.setServiceName(serviceName);
    span.setMethodName(methodName);
    span.setId(id);
    span.setParentId(pid);
    span.setTraceId(traceId);
    span.setSample(sample);

    return span;
  }

  // 构件rootSpan,是否采样
  Span newSpan(final String appName, final String serviceName, final String methodName) {
    final Span span = new Span();
    span.setAppName(appName);
    span.setServiceName(serviceName);
    span.setMethodName(methodName);
    if (this.isSample()) {
      span.setSample(true);
      span.setTraceId(genTracerId());
      span.setId("1");
    } else {
      span.setSample(false);
      span.setTraceId(null);
      span.setId(null);
    }

    return span;
  }

  // 构件rootSpan,是否采样
  Span newSpan(final String appName, final String serviceName, final String methodName, final Span parentSpan) {
    final Span span = new Span();
    span.setAppName(appName);
    span.setServiceName(serviceName);
    span.setMethodName(methodName);
    if (parentSpan.isSample()) {
      final int subSpanNum = parentSpan.getSubSpanNum() + 1;
      parentSpan.setSubSpanNum(subSpanNum);

      span.setSample(true);
      span.setTraceId(parentSpan.getTraceId());
      span.setParentId(parentSpan.getId());
      span.setId(parentSpan.getId() + "." + subSpanNum);
    } else {
      span.setSample(false);
      span.setTraceId(null);
      span.setParentId(null);
      span.setId(null);
    }

    return span;
  }

  public void setSampleRate(final int rate) {
    sampler.setSampleRate(rate);
  }

  boolean isSample() {
    return sampler.isSample();
  }

  void addBinaryAnntation(final BinaryAnnotation bin) {
    final Span span = spanThreadLocal.get();
    if (span != null) {
      span.addBinaryAnnotation(bin);
    }
  }

  // 构件cs annotation
  void clientSendRecord(final Span span, final Endpoint endpoint, final long start) {
    final Annotation annotation = new Annotation();
    annotation.setType(AnnotationType.CLIENT_SEND);
    annotation.setTimestamp(start);
    annotation.setEndpoint(endpoint);
    span.addAnnotation(annotation);
  }

  // 构件cr annotation
  void clientReceiveRecord(final Span span, final Endpoint endpoint, final long end) {
    if (span.isSample() && transfer != null) {
      final Annotation annotation = new Annotation();
      annotation.setType(AnnotationType.CLIENT_RECEIVE);
      annotation.setEndpoint(endpoint);
      annotation.setTimestamp(end);
      span.addAnnotation(annotation);
      transfer.asyncSend(span);
    }
  }

  // 构件sr annotation
  void serverReceiveRecord(final Span span, final Endpoint endpoint, final long start) {
    if (span.isSample()) {
      final Annotation annotation = new Annotation();
      annotation.setType(AnnotationType.SERVER_RECEIVE);
      annotation.setEndpoint(endpoint);
      annotation.setTimestamp(start);
      span.addAnnotation(annotation);
    }

    this.setParentSpan(span);
  }

  // 构件 ss annotation
  void serverSendRecord(final Span span, final Endpoint endpoint, final long end) {
    if (span.isSample() && transfer != null) {
      final Annotation annotation = new Annotation();
      annotation.setTimestamp(end);
      annotation.setEndpoint(endpoint);
      annotation.setType(AnnotationType.SERVER_SEND);
      span.addAnnotation(annotation);
      transfer.asyncSend(span);
    }

    this.removeParentSpan();
  }

  String genTracerId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public void setTransfer(final Transfer transfer) {
    this.transfer = transfer;
  }

  // =======================以下为提供给对外开放的方法=================================

  public void addBinaryAnnotation(final String className, final String methodName, final int duration) {
    final Span span = spanThreadLocal.get();
    if (span != null && StringUtils.isNotBlank(className) && StringUtils.isNotBlank(methodName) && duration >= 0) {
      final BinaryAnnotation binaryAnnotation = new BinaryAnnotation();
      binaryAnnotation.setKey(className);
      binaryAnnotation.setValue(methodName);
      binaryAnnotation.setDuration(duration);
      binaryAnnotation.setTimestamp(System.currentTimeMillis());
      binaryAnnotation.setEndpoint(span.getEndpoint());
      span.addBinaryAnnotation(binaryAnnotation);
    }
  }

  public void addBinaryAnnotation(final String key, final String value) {
    addBinaryAnnotation(key, value, 0);
  }

  public void addBinaryAnnotation(final String className, final String methodName, final Throwable ex) {
    final Span span = spanThreadLocal.get();
    if (span != null && ex != null) {
      final Endpoint endpoint = span.getEndpoint();
      if (endpoint != null) {
        final BinaryAnnotation exAnnotation = new BinaryAnnotation();
        exAnnotation.setThrowable(className, methodName, ex);
        exAnnotation.setEndpoint(endpoint);
        addBinaryAnntation(exAnnotation);
      }
    }
  }

  public String getTraceCtx() {
    String retStr = "";

    final Span span = spanThreadLocal.get();
    if (span != null) {
      final String traceId = span.getTraceId();
      final String parenSpantId = span.getParentId();
      final String spanId = span.getId();
      if (StringUtils.isNotBlank(traceId) && StringUtils.isNotBlank(spanId)) {
        final Map<String, String> retMap = new ConcurrentHashMap<String, String>();
        retMap.put(TracerUtils.TRACE_ID, traceId);
        retMap.put(TracerUtils.PARENT_SPAN_ID, parenSpantId);
        retMap.put(TracerUtils.SPAN_ID, spanId);

        retStr = JSON.toJSONString(retMap);
      }
    }

    return retStr;
  }

  public void setTraceCtx(final String ctx) {
    if (StringUtils.isNotBlank(ctx)) {
      final JSONObject jsonObject = JSON.parseObject(ctx);
      final String traceId = jsonObject.getString(TracerUtils.TRACE_ID);
      final String parenSpantId = jsonObject.getString(TracerUtils.PARENT_SPAN_ID);
      final String spanId = jsonObject.getString(TracerUtils.SPAN_ID);

      if (StringUtils.isNotBlank(traceId) && StringUtils.isNotBlank(spanId)) {
        final Span span = new Span();
        span.setTraceId(traceId);
        span.setParentId(parenSpantId);
        span.setId(spanId);

        ctxTheadLocal.set(span);
      }
    }
  }

  Span getAndRemoveTraceCtx() {
    final Span retSpan = ctxTheadLocal.get();
    ctxTheadLocal.remove();
    return retSpan;
  }
}
