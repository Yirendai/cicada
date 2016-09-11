package com.yirendai.infra.cicada.util.elastic;

import com.yirendai.infra.cicada.config.CicadaCollectorProps;
import com.yirendai.infra.cicada.constants.DateTimeFormats;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class DayRotatedIndexManager implements IndexManager {
  private static final int TIMEOUT_SECOND_LIMITS = 5;
  private static final String SPAN_TYPE_STR = "span";
  private static final String ANNOTATION_TYPE_STR = "annotation";
  private static final String INDEX_CAT_CHARS = "_";

  @Autowired
  private CicadaCollectorProps props;

  @Autowired
  private IndexConfigLoader indexConfigLoader;

  @Autowired
  private TransportClient client;

  private Map<String, String> indexNameMap;

  @PostConstruct
  public void init() {
    this.indexNameMap = new HashMap<String, String>();
    this.indexNameMap.put(SPAN_TYPE_STR, this.props.getEsSpanIndexPrefix());
    this.indexNameMap.put(ANNOTATION_TYPE_STR, this.props.getEsAnnotationIndexPrefix());
  }

  public String getCurrentIndexName(final String type) {
    final String curIndexName = generateCurrentIndexName(type);
    if (!exists(curIndexName)) {
      createIndex(type, curIndexName);
    }

    return curIndexName;
  }

  private String generateCurrentIndexName(final String type) {
    final String dateStr = DateTime.now().toString(DateTimeFormats.FULL_DATE_ENGLISH);
    final String indexNamePrefix = indexNameMap.get(type);
    return indexNamePrefix + INDEX_CAT_CHARS + dateStr;
  }

  private boolean exists(final String indexName) {
    final IndicesExistsRequest req = new IndicesExistsRequest(indexName);
    IndicesExistsResponse resp = null;

    try {
      resp = client.admin().indices().exists(req).get(TIMEOUT_SECOND_LIMITS, TimeUnit.SECONDS);
    } catch (InterruptedException ex) {
      log.error("failed get index's exists status, indexName {}, error {}", indexName, ex);
    } catch (TimeoutException ex) {
      log.error("timeout when get index's exists status, indexName {}, error {}", indexName, ex);
    } catch (ExecutionException ex) {
      log.error("execution exception occured, indexName {}, error {}", indexName, ex);
    }
    
    return resp != null && resp.isExists();
  }

  private void createIndex(final String type, final String indexName) {
    // 加载配置文件
    final String indexConfig = indexConfigLoader.load(type);

    // 创建索引
    try {
      client.admin().indices().prepareCreate(indexName).setSource(indexConfig).execute().actionGet();
    } catch (IndexAlreadyExistsException ex) {
      log.error("conflict while create index: {}, it's not serious.", indexName);
    }
  }
}
