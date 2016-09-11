package com.yirendai.infra.cicada.capture;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 用户自定义采样率实现。 用户在配置文件中指定采样率.
 * 
 * @author Zecheng
 */
@Component
public class CustomSampler implements Sampler {
  private static final int BASE = 100;

  private int sampleRate = 100;

  private final Random randIntGen = new Random();

  public boolean isSample() {
    boolean result = false;
    if (sampleRate > 0) {
      final int randomValue = randIntGen.nextInt(BASE);
      if (randomValue < sampleRate) {
        result = true;
      }
    }

    return result;
  }

  public void setSampleRate(final int rate) {
    if (rate < 0) {
      this.sampleRate = 0;
    } else if (rate > BASE) {
      this.sampleRate = BASE;
    } else {
      this.sampleRate = rate;
    }
  }
}
