package com.yirendai.infra.cicada.capture;

import com.yirendai.infra.cicada.entity.trace.Endpoint;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.utils.IpUtils;
import com.yirendai.infra.cicada.utils.SpringContextUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TraceableAop {

  private static final String DEFAULT_APP_NAME = "traceable";

  @Around("execution(* *(..)) && "
                                + "(@within(com.yirendai.infra.cicada.capture.Traceable) ||"
                               + " @annotation(com.yirendai.infra.cicada.capture.Traceable))")
  public Object around(final ProceedingJoinPoint point) throws java.lang.Throwable {
    final long start = System.currentTimeMillis();
    final String localIp = IpUtils.getRealIpWithStaticCache();
    final int localPort = 0;
    final Endpoint endpoint = new Endpoint(localIp, localPort);
    final String className = point.getTarget().getClass().getCanonicalName();
    final String methodName = point.getSignature().getName();
    final String appName = SpringContextUtil.getAppName(DEFAULT_APP_NAME);

    final Tracer tracer = Tracer.getInstance();
    final Span parentSpan = tracer.getParentSpan();
    final boolean createSpan = parentSpan == null;

    Span span = null;
    if (createSpan) {
      final Span traceCtx = tracer.getAndRemoveTraceCtx();
      if (traceCtx != null) {
        final String traceId = traceCtx.getTraceId();
        final String pid = traceCtx.getParentId();
        final String id = traceCtx.getId();
        final boolean sample = traceId != null;
        span = tracer.genSpan(appName, className, methodName, traceId, pid, id, sample);
      } else {
        span = tracer.newSpan(appName, className, methodName);
      }

      tracer.serverReceiveRecord(span, endpoint, start);
    }

    Object result = null;
    try {
      result = point.proceed();
    } catch (Exception ex) {
      if (createSpan) {
        span.addException(className, methodName, ex, endpoint);
      } else {
        tracer.addBinaryAnnotation(className, methodName, ex);
      }

      throw ex;
    } finally {
      final long end = System.currentTimeMillis();
      if (createSpan) {
        tracer.serverSendRecord(span, endpoint, end);
      } else {
        tracer.addBinaryAnnotation(className, methodName, (int) (end - start));
      }
    }

    return result;
  }

}
