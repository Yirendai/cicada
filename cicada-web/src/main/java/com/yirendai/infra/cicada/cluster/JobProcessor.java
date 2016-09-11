package com.yirendai.infra.cicada.cluster;

import com.alibaba.fastjson.JSON;
import com.yirendai.infra.cicada.entity.Fragment;
import com.yirendai.infra.cicada.entity.Job;
import com.yirendai.infra.cicada.entity.JobSlice;
import com.yirendai.infra.cicada.service.StatisticService;

import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class JobProcessor implements NodeCacheListener {
  @Autowired
  private NodeCache jobCache;

  @Autowired
  private StatisticService service;

  @Override
  public void nodeChanged() {
    final String jobStr = new String(jobCache.getCurrentData().getData());
    final JobSlice slice = genJobSlice(jobStr);
    service.run(slice);
  }

  private JobSlice genJobSlice(final String jobStr) {
    final Job job = JSON.parseObject(jobStr, Job.class);
    final Fragment fragment = job.getJobFragment(ClusterNodeRegister.getNodeLabel());

    final JobSlice jobSlice = new JobSlice();
    jobSlice.setStartTimestamp(job.getStartTimestamp());
    jobSlice.setEndTimestamp(job.getEndTimestamp());
    jobSlice.setStart(fragment.getStart());
    jobSlice.setEnd(fragment.getEnd());

    return jobSlice;
  }
}
