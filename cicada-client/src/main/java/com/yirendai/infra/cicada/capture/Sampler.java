package com.yirendai.infra.cicada.capture;

/**
 * 采样率适配器接口.
 */
public interface Sampler {
  boolean isSample();

  void setSampleRate(int rate);
}
