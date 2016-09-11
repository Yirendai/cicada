package com.yirendai.infra.cicada.util.progress;

import org.springframework.stereotype.Component;

@Component
public interface ProgressRecorder {
  /**
   * 记录进度.
   */
  void set(Progress record);

  /**
   * 读取进度.
   */
  Progress get();
}
