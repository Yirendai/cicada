package com.yirendai.infra.cicada.api.rest;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.constants.ApiDocs;
import com.yirendai.infra.cicada.constants.AppError;
import com.yirendai.infra.cicada.entity.SpanEntity;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.exception.BadRequestException;
import com.yirendai.infra.cicada.request.EntityPageRequest;
import com.yirendai.infra.cicada.service.SpanService;
import com.yirendai.infra.cicada.service.TraceService;

import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Loggable
@RestController
@RequestMapping(value = "/cicada/api/v1/entities")
public class EntityApi {
  @Autowired
  TraceService traceService;

  @Autowired
  SpanService spanService;

  /**
   * 分页获取span.
   */
  @RequestMapping(value = "/spans", method = RequestMethod.POST)
  @ApiOperation(value = ApiDocs.FETCH_SPAN_PAGE, notes = ApiDocs.FETCH_SPAN_PAGE)
  public List<SpanModel> fetchSpansPage(@RequestBody final EntityPageRequest request) {
    if (!request.isValid()) {
      log.error("request invalid error : fetch span pages request's param incomplete!");
      throw new BadRequestException(AppError.INCOMPLETE_PAGE_REQUEST_PARAMS);
    }

    return spanService.fetchSpanPage(request);
  }

  /**
   * 分页获取采集到异常的span信息.
   */
  @RequestMapping(value = "/errorspans", method = RequestMethod.POST)
  @ApiOperation(value = ApiDocs.FETCH_ERROR_SPAN_PAGE, notes = ApiDocs.FETCH_ERROR_SPAN_PAGE)
  public List<SpanModel> fetchErrorSpansPage(@RequestBody final EntityPageRequest request) {
    if (!request.isValid()) {
      log.error("request invalid error : fetch span pages request's param incomplete!");
      throw new BadRequestException(AppError.INCOMPLETE_PAGE_REQUEST_PARAMS);
    }

    return spanService.fetchErrorSpanPage(request);
  }

  /**
   * 根据TraceId获取整个trace调用链的信息.
   */
  @RequestMapping(value = "/traces/{traceId}", method = RequestMethod.GET)
  @ApiOperation(value = ApiDocs.FETCH_TRACE_CHAIN, notes = ApiDocs.FETCH_TRACE_CHAIN)
  public List<SpanEntity> fetchTraceChain(@PathVariable("traceId") final String traceId) {
    return traceService.fetchTraceChain(traceId);
  }
}
