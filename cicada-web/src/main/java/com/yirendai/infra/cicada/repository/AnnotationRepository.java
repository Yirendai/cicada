package com.yirendai.infra.cicada.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yirendai.infra.cicada.configure.CicadaWebProps;
import com.yirendai.infra.cicada.entity.model.AnnotationModel;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class AnnotationRepository {
  @Autowired
  private TransportClient client;
  
  @Autowired
  private CicadaWebProps props;

  /**
   * 根据traceId和spanId获取annotationModel列表.
   */
  public List<AnnotationModel> getSpanAnnotations(final String traceId, final String spanId) {
    // 声明SearchRequestBuilder实例
    final String indice = getDefaultIndice();
    final SearchRequestBuilder builder = client.prepareSearch(indice);
    builder.setTypes(props.getEsTypeName());

    // 设置查询条件
    final BoolQueryBuilder query = new BoolQueryBuilder();
    query.must(QueryBuilders.termQuery("traceId", traceId)) //
        .must(QueryBuilders.termQuery("spanId", spanId));

    // 执行查询
    final SearchResponse response = builder.setQuery(query).execute().actionGet();

    // 处理返回结果
    final List<AnnotationModel> annos = new LinkedList<AnnotationModel>();
    for (final SearchHit hit : response.getHits().hits()) {
      final String docStr = hit.getSourceAsString();
      try {
        final AnnotationModel model = JSON.parseObject(docStr, AnnotationModel.class);
        annos.add(model);
      } catch (JSONException ex) {
        log.error("failed load data {} to AnnotationModel, error {}", docStr, ex);
        continue;
      }
    }

    return annos;
  }

  private String getDefaultIndice() {
    return props.getEsAnnotationIndexPrefix() + "_*";
  }
}
