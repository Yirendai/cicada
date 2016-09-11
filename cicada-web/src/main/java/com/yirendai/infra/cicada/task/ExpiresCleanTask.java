package com.yirendai.infra.cicada.task;

import com.yirendai.infra.cicada.configure.CicadaWebProps;
import com.yirendai.infra.cicada.repository.SpanStatisInfoRepository;
import com.yirendai.infra.cicada.util.ElasticIndexManager;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 清除过期数据，包括ES中的原始数据和mysql中的统计数据.
 * @author Zecheng
 */
@Component
public class ExpiresCleanTask {
  @Autowired
  private CicadaWebProps props;

  @Autowired
  private ElasticIndexManager esIdxMgr;

  @Autowired
  private SpanStatisInfoRepository spanStatisRepo;

  @Scheduled(cron = "0 10 0 * * *")
  public void run() {
    esIdxMgr.removeExpireIndice();
    final DateTime expireTime = DateTime.now().minusDays(props.getEsIndexRetentionDays());
    spanStatisRepo.cleanExpireInfos(expireTime);
  }
}
