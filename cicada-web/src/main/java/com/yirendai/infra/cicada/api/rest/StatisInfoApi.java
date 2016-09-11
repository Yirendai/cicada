package com.yirendai.infra.cicada.api.rest;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.constants.ApiDocs;
import com.yirendai.infra.cicada.constants.AppError;
import com.yirendai.infra.cicada.entity.SpanStatisInfo;
import com.yirendai.infra.cicada.exception.BadRequestException;
import com.yirendai.infra.cicada.request.StatisInfoPageRequest;
import com.yirendai.infra.cicada.request.StatisInfoRequest;
import com.yirendai.infra.cicada.service.SpanStatisInfoService;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Loggable
@RequestMapping(value = "/cicada/api/v1/statisinfos")
public class StatisInfoApi {
  @Autowired
  SpanStatisInfoService spanService;

  /**
   * 获取指定时间段、指定服务的所有Span统计信息.
   */
  @RequestMapping(value = "/spans/all", method = RequestMethod.POST)
  @ApiOperation(value = ApiDocs.FETCH_SPAN_STATIS_INFO_PAGE, notes = ApiDocs.FETCH_SPAN_STATIS_INFO_PAGE)
  public List<SpanStatisInfo> fetchSpanStatisInfoByDuration(@RequestBody final StatisInfoRequest request) {
    return spanService.fetchAllByDuration(request);
  }

  /**
   * 获取指定时间段、指定服务的分页Span统计信息.
   */
  @RequestMapping(value = "/spans", method = RequestMethod.POST)
  @ApiOperation(value = ApiDocs.FETCH_SPAN_STATIS_INFO_PAGE, notes = ApiDocs.FETCH_SPAN_STATIS_INFO_PAGE)
  public Page<SpanStatisInfo> fetchSpanStatisInfoPageByDuration(@RequestBody final StatisInfoPageRequest request) {
    if (!request.isValid()) {
      throw new BadRequestException(AppError.INCOMPLETE_PAGE_REQUEST_PARAMS);
    }

    final Pageable pageable = new PageRequest(request.getPage(), request.getSize());
    return spanService.fetchPage(request, pageable);
  }
}
