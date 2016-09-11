package com.yirendai.infra.cicada.config;

import com.yirendai.infra.cicada.transfer.TransferEngine;
import com.yirendai.infra.cicada.utils.SpringContextUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CicadaConfig {

  @Value("${cicada.url}")
  private String url;

  @Value("${cicada.sampleRate:100}")
  private int sampleRate;

  @Value("${cicada.connectTimeout:100}")
  private int connectTimeout;

  @Value("${cicada.soTimeout:100}")
  private int soTimeout;

  @Value("${cicada.batchSize:32}")
  private int batchSize;

  @Value("${cicada.bufferSize:1024}")
  private int bufferSize;

  @Value("${cicada.tpsLimit:2048}")
  private int tpsLimit;

  @Bean(name = "transferEngine", initMethod = "start")
  public TransferEngine transferEngine() {
    final TransferEngine transferEngine = new TransferEngine(url);

    transferEngine.setSampleRate(sampleRate);
    transferEngine.setConnectTimeout(connectTimeout);
    transferEngine.setSoTimeout(soTimeout);
    transferEngine.setBatchSize(batchSize);
    transferEngine.setBufferSize(bufferSize);
    transferEngine.setTpsLimit(tpsLimit);

    return transferEngine;
  }

  @Bean
  public SpringContextUtil springContextUtil() {
    return new SpringContextUtil();
  }

}
