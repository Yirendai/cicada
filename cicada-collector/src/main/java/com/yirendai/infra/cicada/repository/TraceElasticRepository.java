package com.yirendai.infra.cicada.repository;

import com.alibaba.fastjson.JSON;
import com.yirendai.infra.cicada.config.CicadaCollectorProps;
import com.yirendai.infra.cicada.util.elastic.IndexManager;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TraceElasticRepository {
  @Autowired
  private CicadaCollectorProps props;

  @Autowired
  private TransportClient client;

  @Autowired
  private IndexManager indexManager;

  public <T> void upload(final String type, final List<T> objects) {
    final String indexName = indexManager.getCurrentIndexName(type); 
    final BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
      @Override
      public void beforeBulk(final long executionId, final BulkRequest request) { }

      @Override
      public void afterBulk(final long executionId, //
          final BulkRequest request,  //
          final BulkResponse response) {
        if (response.hasFailures()) {
          log.error("failed index data; {}", response.buildFailureMessage());
        }
      }

      @Override
      public void afterBulk(final long executionId, //
          final BulkRequest request, //
          final Throwable failure) {
        log.error("failed upload data: {}", failure.getCause());
      }
    }).build();

    // add all datas to bulkProcessor
    for (final T object : objects) {
      bulkProcessor.add(new IndexRequest(indexName, props.getEsTypeName()).source(JSON.toJSONString(object)));
    }

    try {
      bulkProcessor.awaitClose(props.getEsBulkAwaitMinutes(), TimeUnit.MINUTES);
    } catch (InterruptedException ex) {
      log.error("execute bulkProcessor interrupted, type {}, error msg: {}", indexName, ex);
    }
  }
}
