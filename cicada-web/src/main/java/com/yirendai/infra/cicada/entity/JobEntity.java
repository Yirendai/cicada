package com.yirendai.infra.cicada.entity;

import lombok.Data;

@Data
public class JobEntity {
  private long jobId;
  private String key;
  private String desc; // 任务描述信息
  private String status; // 任务完成状态（完成、异常中断）
}
