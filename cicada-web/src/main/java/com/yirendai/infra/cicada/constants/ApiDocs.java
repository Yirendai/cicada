package com.yirendai.infra.cicada.constants;

public class ApiDocs {
  // for ProjectInfoServiceApi
  public static final String SEARCH_APPS_PAGE = "分页查看所有项目信息";
  public static final String SEARCH_APP_SERVICES_PAGE = "分页查询app下的service信息";
  public static final String SEARCH_APP_SERVICE_METHODS_PAGE = "分页获取service下的method详情";

  // for StatisInfoApi
  public static final String FETCH_TRACE_STATIS_INFO_PAGE = "获取指定时间段、指定服务的所有trace统计信息";
  public static final String FETCH_SPAN_STATIS_INFO_PAGE = "获取指定时间段、指定服务的所有Span统计信息";

  // for EntityApi
  public static final String FETCH_TRACE_PAGE = "分页获取Trace信息";
  public static final String FETCH_SPAN_PAGE = "分页获取Span信息";
  public static final String FETCH_ERROR_SPAN_PAGE = "分页获取异常Span信息";
  public static final String FETCH_TRACE_CHAIN = "根据TraceId获取整个trace调用链的信息";

}
