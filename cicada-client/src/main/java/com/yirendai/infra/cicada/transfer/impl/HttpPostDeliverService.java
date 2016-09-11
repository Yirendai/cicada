package com.yirendai.infra.cicada.transfer.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.transfer.DeliverService;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HttpPostDeliverService implements DeliverService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpPostDeliverService.class);

  // private CloseableHttpClient httpClient;
  private final CloseableHttpAsyncClient httpClient;
  private final HttpPost httpPost;

  public HttpPostDeliverService(final String postUrl, final int connectTimeout, final int soTimeout) {
    httpClient = HttpAsyncClients.createDefault();
    httpClient.start();

    httpPost = new HttpPost(postUrl);
    final RequestConfig requestConfig =
        RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(soTimeout).build();
    httpPost.setConfig(requestConfig);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Content-Type", "text/html;charset=UTF-8");
  }

  public boolean deliver(final Span span) {
    boolean ret = false;
    if (span == null) {
      LOGGER.error("no span!");
      ret = false;
    } else {
      final List<Span> spanList = new ArrayList<Span>();
      spanList.add(span);

      ret = deliver(spanList);
    }

    return ret;
  }

  public boolean deliver(final List<Span> spanList) {
    boolean ret = false;

    if (CollectionUtils.isEmpty(spanList)) {
      LOGGER.error("spanList is Empty!");
      ret = false;
    } else {
      if (httpClient == null || httpPost == null) {
        LOGGER.error("httpClient({}) or httpPost({}) is null", httpClient, httpPost);
        ret = false;
      } else {
        final long startTime = System.currentTimeMillis();

        final int spanSize = spanList.size();
        final String spanListJson = JSON.toJSONString(spanList);
        final StringEntity postingString = new StringEntity(spanListJson, "utf-8");

        httpPost.setEntity(postingString);
        httpClient.execute(httpPost, new FutureCallback<HttpResponse>() {
          public void completed(final HttpResponse response) {
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("[push({})] [http_status:200] [spanSize:{}] [{}ms]", spanListJson, spanSize,
                  (System.currentTimeMillis() - startTime));
            }
          }

          public void failed(final Exception ex) {
            LOGGER.error("[push({})] [{}] [error:{}]", httpPost.getURI(), spanListJson, ex);
          }

          public void cancelled() {
            LOGGER.error("[push({})] [http_status:cancelled]  [{}ms]", spanListJson,
                (System.currentTimeMillis() - startTime));
          }
        });

        ret = true;
      }
    }

    return ret;
  }
}
