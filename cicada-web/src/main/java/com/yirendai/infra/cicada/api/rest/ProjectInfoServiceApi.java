package com.yirendai.infra.cicada.api.rest;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.constants.ApiDocs;
import com.yirendai.infra.cicada.entity.AppInfo;
import com.yirendai.infra.cicada.entity.MethodInfo;
import com.yirendai.infra.cicada.entity.ServiceInfo;
import com.yirendai.infra.cicada.service.AppService;
import com.yirendai.infra.cicada.service.MethodService;
import com.yirendai.infra.cicada.service.ServiceService;

import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Loggable
@RestController
@RequestMapping(value = "/cicada/api/v1")
public class ProjectInfoServiceApi {
  public static final int DEFAULT_PAGE_NUM = 0;
  public static final int DEFAULT_PAGE_SIZE = 20;

  @Autowired
  private AppService appService;

  @Autowired
  private ServiceService serviceService;

  @Autowired
  private MethodService methodService;

  /**
   * 查询apps分页.
   */
  @RequestMapping(value = "/projects/apps", method = RequestMethod.GET)
  @ApiOperation(value = ApiDocs.SEARCH_APPS_PAGE, notes = ApiDocs.SEARCH_APPS_PAGE)
  public Page<AppInfo> searchApps(@RequestParam("searchText") final String searchText, //
      @PageableDefault(page = DEFAULT_PAGE_NUM, size = DEFAULT_PAGE_SIZE,  //
          sort = "id", direction = Direction.ASC) final Pageable pageable) { 

    final Page<AppInfo> pageResult;
    if (StringUtils.isBlank(searchText)) {
      pageResult = appService.fetchAppInfos(pageable);
    } else {
      pageResult = appService.findByAppNameLike(searchText, pageable);
    }

    return pageResult;
  }

  /**
   * 分页查询app下的service信息.
   */
  @RequestMapping(value = "/projects/services", method = RequestMethod.GET)
  @ApiOperation(value = ApiDocs.SEARCH_APP_SERVICES_PAGE, notes = ApiDocs.SEARCH_APP_SERVICES_PAGE)
  public Page<ServiceInfo> searchServices(@RequestParam("appId") final int appId, //
      @RequestParam("searchText") final String searchText, //
      @PageableDefault(page = DEFAULT_PAGE_NUM, size = DEFAULT_PAGE_SIZE,  //
          sort = "id", direction = Direction.ASC) final Pageable pageable) { 

    final Page<ServiceInfo> pageResult;
    if (StringUtils.isBlank(searchText)) {
      pageResult = serviceService.getServicesByAppId(appId, pageable);
    } else {
      pageResult = serviceService.findByAppIdAndserviceNameLike(appId, searchText, pageable);
    }

    return pageResult;
  }

  /**
   * 分页获取service下的method详情.
   */
  @RequestMapping(value = "/projects/methods", method = RequestMethod.GET)
  @ApiOperation(value = ApiDocs.SEARCH_APP_SERVICE_METHODS_PAGE, //
      notes = ApiDocs.SEARCH_APP_SERVICE_METHODS_PAGE)
  public Page<MethodInfo> searchMethods(@RequestParam("serviceId") final int serviceId, //
      @RequestParam("searchText") final String searchText, //
      @PageableDefault(page = DEFAULT_PAGE_NUM, size = DEFAULT_PAGE_SIZE,  //
          sort = "id", direction = Direction.ASC) final Pageable pageable) { 

    final Page<MethodInfo> pageResult;
    if (StringUtils.isBlank(searchText)) {
      pageResult = methodService.getMethodsByService(serviceId, pageable);
    } else {
      pageResult = methodService.findByServiceIdAndMethodNameLike(serviceId, searchText, pageable);
    }
    return pageResult;
  }
}
