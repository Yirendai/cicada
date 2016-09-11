package com.yirendai.infra.cicada.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yirendai.infra.cicada.configure.CicadaWebProps;
import com.yirendai.infra.cicada.entity.Fragment;
import com.yirendai.infra.cicada.entity.Job;
import com.yirendai.infra.cicada.util.ZookeeperUtil;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class JobDispatcher {
  private static final String CICADA_JOBNODE_DEFAULT_CONTENT = "jobNode";
  private static final long TASK_INTERVAL_MILLIS = 300L * 1000; 

  @Autowired
  private CicadaWebProps props;

  @Autowired
  private CuratorFramework zkClient;
  
  @Setter
  private String jobNodePath;
  
  @Scheduled(fixedRate = TASK_INTERVAL_MILLIS)
  public void dispatch() {
    // 非主节点不参与任务分发
    if (!ClusterLeaderManager.isLeader()) {
      return;
    }

    final Job job = new Job();
    final List<String> instances = listNodes();

    // 设置任务分片信息
    final int size = instances.size();
    for (int i = 0; i < size; ++i) {
      final Fragment fragment = new Fragment();
      fragment.setStart(props.getJobSlotRange() * i / size);
      fragment.setEnd(props.getJobSlotRange() * (i + 1) / size);
      job.addJobFragment(instances.get(i), fragment);
    }

    // 设置任务其他属性
    long jobId = 1;
    final long endTimestamp = System.currentTimeMillis();
    long startTimestamp = endTimestamp - TASK_INTERVAL_MILLIS;
    final Job lastJob = getJob();
    if (lastJob != null) {
      startTimestamp = lastJob.getEndTimestamp();
      lastJob.incrJodId();
      jobId = lastJob.getId();
    }
    job.setId(jobId);
    job.setStartTimestamp(startTimestamp);
    job.setEndTimestamp(endTimestamp);

    // 将任务信息更新到zookeeper
    putJob(job);
  }

  public void putJob(final Job job) {

    final String jobStr = JSON.toJSONString(job);
    final String jobNodePath = props.getJobNodePath();

    try {
      if (!ZookeeperUtil.exists(zkClient, props.getJobNodePath())) {
        ZookeeperUtil.create(zkClient, props.getJobNodePath(), jobStr);
        return;
      }
      zkClient.setData().forPath(jobNodePath, jobStr.getBytes());
    } catch (Exception ex) {
      log.error("failed put job to zookeeper, error: {}", ex);
      // add throw new RuntimeException code here!
    }
  }

  public Job getJob() {
    final String jobNodePath = props.getJobNodePath();
    Job job = null;
    String lastJobStr = null;

    try {
      final byte[] lastJobByteArr = zkClient.getData().forPath(jobNodePath);
      lastJobStr = new String(lastJobByteArr);
      if (!lastJobStr.equals(CICADA_JOBNODE_DEFAULT_CONTENT)) {
        job = JSON.parseObject(lastJobStr, Job.class);
      }
    } catch (JSONException ex) {
      log.error("failed parse jsonStr to object, json: {}, error: {}", lastJobStr, ex);
    } catch (Exception ex) {
      log.error("failed get job info, path: {}, error: {}", jobNodePath, ex);
    }

    return job;
  }

  private List<String> listNodes() {
    List<String> nodes = null;
    final String instancesNodePath = props.getInstancesNodePath();

    try {
      nodes = zkClient.getChildren().forPath(instancesNodePath);
    } catch (Exception ex) {
      log.error("failed get instance list of {}, error {}", instancesNodePath, ex);

      // 如果捕获到异常，只在节点列表里添加自身
      nodes = new LinkedList<String>();
      nodes.add(ClusterNodeRegister.getNodeLabel());
    }

    return nodes;
  }
}
