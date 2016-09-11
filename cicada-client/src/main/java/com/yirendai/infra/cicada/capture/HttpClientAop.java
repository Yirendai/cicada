package com.yirendai.infra.cicada.capture;

import org.apache.http.client.methods.HttpUriRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class HttpClientAop {

  /**
   * 拦截HttpClient的Post与Get方法.
   * 
   */
  @Around("execution(* org.apache.http.client.HttpClient.execute(..)) && args(httpUriRequest)")
  public Object around(final ProceedingJoinPoint proceedingJoinPoint, final HttpUriRequest httpUriRequest)
      throws java.lang.Throwable {
    final long startTime = System.currentTimeMillis();
    final Object[] args = proceedingJoinPoint.getArgs();
    final Object result = proceedingJoinPoint.proceed(args);

    if (httpUriRequest instanceof HttpUriRequest) {
      final String methodName = httpUriRequest.getMethod();
      final String className = httpUriRequest.getURI().toString();

      Tracer.getInstance().addBinaryAnnotation(className, methodName, (int) (System.currentTimeMillis() - startTime));
    }

    return result;
  }
}
