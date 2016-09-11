package com.yirendai.infra.cicada.capture;

import com.yirendai.infra.cicada.entity.trace.Endpoint;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.utils.IpUtils;
import com.yirendai.infra.cicada.utils.SpringContextUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "cicadaHttpFilter", urlPatterns = {"*"},
    initParams = {@WebInitParam(name = "uriConditions", value = "*", description = "匹配的uri"),
        @WebInitParam(name = "uriNotMatchConditions", description = "不配置的uri",
            value = "**.html,**.htm,**.js,**.gif,**.css,**.rtt,**.dsp,**.jpg,**.png,**.mp4,"
                + "**.flv,**.doc,**.docx,**.xls,**.xlsx,**.ppt,**.pptx,**.pdf,**.ico")})
@SuppressWarnings("PMD.CyclomaticComplexity")
public class HttpFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(HttpFilter.class);

  // 需要拦截的正则表达式
  private final List<String> uriConditionRegexList = new ArrayList<String>();
  // 不需要拦截的正则表达式
  private final List<String> uriNotMatchConditionsRegexList = new ArrayList<String>();

  private final Tracer tracer = Tracer.getInstance();

  private static final String DEFAULT_APP_NAME = "http";

  public void destroy() {
    // do nothing
  }

  @SuppressWarnings("PMD.NPathComplexity")
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
      LOG.error("SessionUserFilter only supports HTTP requests");
      throw new ServletException("SessionUserFilter only supports HTTP requests");
    }
    final long start = System.currentTimeMillis();

    final HttpServletRequest req = (HttpServletRequest) request;
    final HttpServletResponse res = (HttpServletResponse) response;
    final String uri = req.getRequestURI();
    if (req.getCharacterEncoding() == null || req.getCharacterEncoding().equalsIgnoreCase("ISO-8859-1")) {
      req.setCharacterEncoding("utf-8");
    }
    if (res.getCharacterEncoding() == null || res.getCharacterEncoding().equalsIgnoreCase("ISO-8859-1")) {
      res.setCharacterEncoding("utf-8");
    }

    // 验证URL
    if (needFilter(uri)) {
      final String localIp = IpUtils.getRealIpWithStaticCache();
      final int localPort = req.getLocalPort();
      final Endpoint endpoint = new Endpoint(localIp, localPort);
      final String url = req.getRequestURI();
      final String httpMethod = req.getMethod();
      final String appName = SpringContextUtil.getAppName(DEFAULT_APP_NAME);

      final Span parentSpan = tracer.getParentSpan();
      Span span;
      if (parentSpan == null) {
        span = tracer.newSpan(appName, url, httpMethod);
      } else {
        span = tracer.newSpan(appName, url, httpMethod, parentSpan);
      }
      tracer.serverReceiveRecord(span, endpoint, start);

      try {
        chain.doFilter(request, response);
      } catch (IOException ex) {
        span.addException(url, httpMethod, ex, endpoint);
        throw ex;
      } catch (ServletException ex) {
        span.addException(url, httpMethod, ex, endpoint);
        throw ex;
      } finally {
        final long end = System.currentTimeMillis();
        tracer.serverSendRecord(span, endpoint, end);
      }
    } else {
      // URL拦截未通过,直接执行doFilter
      chain.doFilter(request, response);
    }
  }

  public void init(final FilterConfig config) throws ServletException {
    final String uriConditions = config.getInitParameter("uriConditions");
    final String uriNotMatchConditions = config.getInitParameter("uriNotMatchConditions");

    fillList(uriConditionRegexList, uriConditions);
    fillList(uriNotMatchConditionsRegexList, uriNotMatchConditions);
  }

  private void fillList(final List<String> targetList, final String configPara) {
    if (StringUtils.isBlank(configPara)) {
      return;
    }

    final String[] conditionSplits = configPara.split("[,|;]");
    for (String condition : conditionSplits) {
      condition = condition.trim();
      if (StringUtils.isBlank(condition)) {
        continue;
      }

      targetList.add(toRegexString(condition));
    }
  }

  @SuppressWarnings({"PMD.AvoidReassigningParameters", "PMD.AvoidPrefixingMethodParameters"})
  public String toRegexString(String inStr) {
    String ret = inStr;
    if (StringUtils.isBlank(inStr)) {
      ret = inStr;
    } else {

      if ("*".equals(inStr)) {
        inStr = "**";
      }

      if (inStr.length() > 1 && inStr.charAt(0) == '*') {
        ret = inStr.replaceAll("\\*", "\\[\\\\S\\| \\]\\*") + "$";
      } else {
        ret = inStr.replaceAll("\\*\\*", "\\[\\\\S\\| \\]\\ACCECC").replaceAll("\\*", "\\[\\\\w\\| |\\%\\|\\\\.\\]\\*")
            .replaceAll("ACCECC", "\\*") + "$";
      }
    }

    return ret;
  }

  public boolean canMatch(final String uri, final List<String> conditions) {
    boolean ret = false;
    if (CollectionUtils.isNotEmpty(conditions)) {
      for (int i = 0; i < conditions.size(); ++i) {
        final String condition = conditions.get(i);
        if (uri.matches(condition)) {
          ret = true;
        }
      }
    }

    return ret;
  }

  public boolean needFilter(final String uri) {
    boolean ret = false;

    if (StringUtils.isNotBlank(uri)) {
      if (canMatch(uri, this.uriNotMatchConditionsRegexList)) {
        ret = false;
      } else if (canMatch(uri, this.uriConditionRegexList)) {
        ret = true;
      }
    }

    return ret;
  }
}
