package com.yirendai.infra.cicada.transfer;

import com.yirendai.infra.cicada.entity.trace.Span;

public interface Transfer {
  boolean isReady();

  boolean isServiceReady(String serviceName);

  void start() throws Exception;

  void cancel();

  void asyncSend(Span span);
}
