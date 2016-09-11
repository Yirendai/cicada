package com.yirendai.infra.cicada.task;

import com.yirendai.infra.cicada.entity.SpanStatisInfo;
import com.yirendai.infra.cicada.entity.model.SpanModel;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于统计trace和span的相关信息，并存储数据库.
 * @author Zecheng
 */
public class LogStatistician {
  private static final double RATE_95 = 0.95;
  private static final double RATE_999 = 0.999;
  private static final String KEY_SEPERATE = "_";

  private final DateTime statisTime;

  private final List<SpanStatisInfo> spanStatisInfos;
  private final Map<String, List<SpanModel>> spanMap;

  public LogStatistician(final DateTime statisTime) {
    this.statisTime = statisTime;
    this.spanStatisInfos = new LinkedList<SpanStatisInfo>();
    this.spanMap = new HashMap<String, List<SpanModel>>();
  }

  public List<SpanStatisInfo> getSpanStatisInfos() {
    return spanStatisInfos;
  }

  public void statistic(final List<SpanModel> models) {
    for (final SpanModel spanModel : models) {
      addSpan(spanModel);
    }

    for (final Map.Entry<String, List<SpanModel>> entry : spanMap.entrySet()) {
      buildSpanStatisInfo(entry.getKey(), entry.getValue());
    }
  }

  private void buildSpanStatisInfo(final String key, final List<SpanModel> spans) {
    Collections.sort(spans, new Comparator<SpanModel>() {
      public int compare(final SpanModel span1, final SpanModel span2) {
        return span1.getDurationServer() - span2.getDurationServer();
      }
    });

    final String[] ids = key.split("_");
    final SpanStatisInfo info = new SpanStatisInfo();
    info.setStatisTime(statisTime);
    info.setAppId(Integer.parseInt(ids[0]));
    info.setServiceId(Integer.parseInt(ids[1]));
    info.setMethodId(Integer.parseInt(ids[2]));

    info.setCount(spans.size());
    info.setMinDuration(spans.get(0).getDurationServer());
    info.setMaxDuration(spans.get(spans.size() - 1).getDurationServer());

    int line95Pos = (int) (spans.size() * RATE_95) - 1;
    if (line95Pos < 0) {
      line95Pos = 0;
    }
    info.setLine95Duration(spans.get(line95Pos).getDurationServer());

    int line999Pos = (int) (spans.size() * RATE_999) - 1;
    if (line999Pos < 0) {
      line999Pos = 0;
    }
    info.setLine999Duration(spans.get(line999Pos).getDurationServer());
    traverseSpanStatistic(spans, info);

    spanStatisInfos.add(info);
  }

  private void traverseSpanStatistic(final List<SpanModel> spans, final SpanStatisInfo info) {
    if (spans.isEmpty()) {
      return;
    }

    double avg = 0.0;
    int index = 1;
    int exceptionCount = 0;
    for (final SpanModel span : spans) {
      avg = (index - 1) / (double) index * avg + (double) span.getDurationServer() / index;
      ++index;
      if (span.isHasException()) {
        ++exceptionCount;
      }
    }

    final double failureRate = (double) exceptionCount / (index - 1);

    info.setAvgDuration(avg);
    info.setFailureRate(failureRate);
  }

  private void addSpan(final SpanModel span) {
    final StringBuilder sb = new StringBuilder();
    sb.append(span.getAppId()).append(KEY_SEPERATE) //
        .append(span.getServiceId()).append(KEY_SEPERATE) //
        .append(span.getMethodId());

    final String key = sb.toString();
    List<SpanModel> spans = spanMap.get(key);
    if (spans == null) {
      spans = new LinkedList<SpanModel>();
      spanMap.put(key, spans);
    }

    spans.add(span);
  }
}
