package com.yirendai.infra.cicada;

import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.transfer.impl.HttpPostDeliverService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpPostDeliverServiceTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpPostDeliverServiceTest.class);
  
  private HttpPostDeliverServiceTest(){}

  public static void main(final String... args) {
    final String url = "http://localhost:9080/upload";
    final int connectTimeout = 10000;
    final int soTimeout = 10000;

    final HttpPostDeliverService service = new HttpPostDeliverService(url, connectTimeout, soTimeout);

    final long startTime = System.currentTimeMillis();
    final Span span = new Span();
    span.setId("1");
    for (int i = 0; i < 10; i++) {
      service.deliver(span);
    }

    LOGGER.info("time:" + (System.currentTimeMillis() - startTime));
  }
}
