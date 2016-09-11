package com.yirendai.infra.cicada.service;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.entity.JobSlice;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.repository.SpanRepository;
import com.yirendai.infra.cicada.repository.StatisInfoBulkRepository;
import com.yirendai.infra.cicada.task.LogStatistician;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 统计操作，定期收集日志，统计结果存储数据库.
 * @author Zecheng
 *
 */
@Component
@Loggable
public class StatisticService {
  @Autowired
  StatisInfoBulkRepository bulkRepo;

  @Autowired
  SpanRepository spanRepo;

  public void run(final JobSlice jobSlice) {
    process(jobSlice);
  }

  private void process(final JobSlice jobSlice) {
    // 收集日志
    final List<SpanModel> models = spanRepo.collectSpan(jobSlice);
    if (models == null) {
      return;
    }

    // 统计
    final DateTime curTime = DateTime.now();
    final LogStatistician statistician = new LogStatistician(curTime);
    statistician.statistic(models);

    // 统计结果入库
    bulkRepo.saveAll(statistician.getSpanStatisInfos());
  }
}
