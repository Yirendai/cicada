package com.yirendai.infra.cicada.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.configure.CicadaWebProps;
import com.yirendai.infra.cicada.constants.DateTimeFormats;
import com.yirendai.infra.cicada.entity.JobSlice;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.request.EntityPageRequest;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Loggable
@Component
public class SpanRepository {
  private static final int TIMEOUT_SECOND_LIMITS = 5;
  private static final String INDEX_CAT_CHARS = "_";
  private static final int ES_SCROLL_KEEP_ALIVE_MILLIS = 60000;
  private static final int ES_SCROLL_SIZE = 9000;

  @Autowired
  private CicadaWebProps props;

  @Autowired
  private TransportClient client;

  /**
   * 查询带异常信息的span分页.
   */
  public List<SpanModel> fetchErrorSpanModelPage(final EntityPageRequest request) {
    // 构造SearchRequestBuilder实例
    final SearchRequestBuilder builder = getSearchBuilder(request);
    final List<SpanModel> pageResult;
    if (builder == null) {
      pageResult = null;
    } else {

      // 设置查询条件
      final BoolQueryBuilder query = new BoolQueryBuilder();
      query.must(QueryBuilders.termQuery("methodId", request.getMethodId()))
          .must(QueryBuilders.termQuery("hasException", true))
          .filter(QueryBuilders.rangeQuery("startTime").from(request.getBeginTime().getMillis())
              .to(request.getEndTime().getMillis()))
          .filter(QueryBuilders.rangeQuery("durationServer").from(request.getFloorDuration())
              .to(request.getCeilDuration()));

      final SearchResponse response = builder.setQuery(query).execute().actionGet();
      pageResult = parseResponse(response);
    }
    return pageResult;
  }

  /**
   * 查询spanModel分页.
   */
  public List<SpanModel> fetchSpanModelPage(final EntityPageRequest request) {

    // 构造SearchRequestBuilder实例
    final List<SpanModel> pageResult;
    final SearchRequestBuilder builder = getSearchBuilder(request);
    if (builder == null) {
      pageResult = null;
    } else {

      // 设置查询条件
      final BoolQueryBuilder query = new BoolQueryBuilder();
      query.must(QueryBuilders.termQuery("methodId", request.getMethodId()))
          .filter(QueryBuilders.rangeQuery("startTime").from(request.getBeginTime().getMillis())
              .to(request.getEndTime().getMillis()))
          .filter(QueryBuilders.rangeQuery("durationServer").from(request.getFloorDuration())
              .to(request.getCeilDuration()));

      final SearchResponse response = builder.setQuery(query).execute().actionGet();
      pageResult = parseResponse(response);
    }
    return pageResult;
  }

  /**
   * 根据TraceId获取所有的SpanModel信息.
   */
  public List<SpanModel> getTraceSpanModels(final String traceId) {
    // 创建SearchRequestBuilder实例，设置builder属性
    final String indice = getDefaultIndice();
    final SearchRequestBuilder builder = client.prepareSearch(indice);
    builder.setTypes(props.getEsTypeName());

    // 设置查询条件
    final BoolQueryBuilder query = new BoolQueryBuilder();
    query.must(QueryBuilders.termQuery("traceId", traceId));

    // 执行查询
    final SearchResponse response = builder.setQuery(query).execute().actionGet();
    return parseResponse(response);
  }

  /**
   * 根据Span收集条件，获取用于统计的数据.
   */
  public List<SpanModel> collectSpan(final JobSlice jobSlice) {
    // 获取需要进行查询的索引名
    final DateTime beginTime = new DateTime(jobSlice.getStartTimestamp());
    final DateTime endTime = new DateTime(jobSlice.getEndTimestamp());
    final String indice = getIndice(beginTime, endTime);

    List<SpanModel> allSpans; //
    if (indice == null) {
      allSpans = null;
    } else {

      // 设置查询条件
      final BoolQueryBuilder query = new BoolQueryBuilder();
      query.must(QueryBuilders.rangeQuery("startTime") //
          .from(jobSlice.getStartTimestamp()).to(jobSlice.getEndTimestamp())) //
          .filter(QueryBuilders.rangeQuery("sliceNo") //
              .from(jobSlice.getStart()).to(jobSlice.getEnd()));

      // 进行查询
      SearchResponse scrollResp = client.prepareSearch(indice) //
          .setTypes(props.getEsTypeName()) //
          .setScroll(new TimeValue(ES_SCROLL_KEEP_ALIVE_MILLIS)) //
          .setQuery(query).setSize(ES_SCROLL_SIZE) //
          .execute().actionGet();

      // 处理查询结果
      allSpans = new LinkedList<SpanModel>();
      final TimeValue tv = new TimeValue(60000);
      while (true) {
        final List<SpanModel> spans = parseResponse(scrollResp);
        allSpans.addAll(spans);

        scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()) //
            .setScroll(tv).execute().actionGet();

        // Break condition: No hits are returned
        if (scrollResp.getHits().getHits().length == 0) {
          break;
        }
      }
    }

    return allSpans;
  }

  private SearchRequestBuilder getSearchBuilder(final EntityPageRequest request) {
    // 创建SearchRequestBuilder实例
    final String indice = getIndice(request.getBeginTime(), request.getEndTime());
    final SearchRequestBuilder builder;
    if (indice == null) {
      builder = null;
    } else {
      builder = client.prepareSearch(indice);
      // 设置builder属性
      builder.setTypes(props.getEsTypeName()) //
          .setFrom(request.getPage() * request.getSize()).setSize(request.getSize());
    }

    return builder;
  }

  private List<SpanModel> parseResponse(final SearchResponse response) {
    // 处理返回结果
    final List<SpanModel> spans = new LinkedList<SpanModel>();
    for (final SearchHit hit : response.getHits().hits()) {
      final String doc = hit.getSourceAsString();
      try {
        spans.add(JSON.parseObject(doc, SpanModel.class));
      } catch (JSONException ex) {
        log.error("failed load data {}, error {}", doc, ex);
        continue;
      }
    }

    return spans;
  }

  /**
   * <p>
   * 根据起始时间和结束时间，获取包含这个时间段的目标index 如果起始时间和结束时间在同一天，返回这一天的索引名称 span_$date.
   * 如果起始时间和结束时间不在同一天，返回全部索引的通配符表达式 span_*.
   * </p>
   */
  private String getIndice(final DateTime beginTime, final DateTime endTime) {
    final int beginDay = beginTime.getDayOfYear();
    final int endDay = endTime.getDayOfYear();

    String indexName;
    if (beginDay == endDay) {
      final String dateStr = endTime.toString(DateTimeFormats.FULL_DATE_ENGLISH);
      indexName = props.getEsSpanIndexPrefix() + INDEX_CAT_CHARS + dateStr;
      if (!exists(indexName)) {
        indexName = null;
      }
    } else {
      indexName = getDefaultIndice();
    }

    return indexName;
  }

  private String getDefaultIndice() {
    return props.getEsSpanIndexPrefix() + INDEX_CAT_CHARS + "*";
  }

  private boolean exists(final String indexName) {
    final IndicesExistsRequest req = new IndicesExistsRequest(indexName);
    boolean isExists = false;

    try {
      final IndicesExistsResponse resp = client.admin() //
          .indices().exists(req) //
          .get(TIMEOUT_SECOND_LIMITS, TimeUnit.SECONDS);
      isExists = resp.isExists();
    } catch (InterruptedException ex) {
      log.error("failed get index's exists status, indexName {}, error {}", indexName, ex);
    } catch (TimeoutException ex) {
      log.error("timeout when get index's exists status, indexName {}, error {}", indexName, ex);
    } catch (ExecutionException ex) {
      log.error("execution exception occured, indexName {}, error {}", indexName, ex);
    }

    return isExists;
  }
}
