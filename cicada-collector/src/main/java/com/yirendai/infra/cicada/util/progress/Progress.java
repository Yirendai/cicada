package com.yirendai.infra.cicada.util.progress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进度信息.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Progress {
  private String fileName;
  private long offset;

  @Override
  public String toString() {
    return "{fileName:" + fileName + ",offset:" + offset + "}";
  }
}
