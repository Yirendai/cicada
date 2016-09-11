/***************************************************************
 * 
 * Copyright (c) 2016 yirendai.com, Inc. All Rights Reserved
 *
 ***************************************************************/

/**
 * @file CollectEntity.java
 * @author melody(zechengzhao@yirendai.com)
 * @date 2016年4月6日
 * @brief collector端与统计端传输的数据结构
 */

package com.yirendai.infra.cicada.entity;

import com.yirendai.infra.cicada.entity.model.SpanModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectEntity {
  public List<SpanModel> spans;
}
