package com.yirendai.infra.cicada.transfer;

import com.yirendai.infra.cicada.capture.CicadaDubboFilter;
import com.yirendai.infra.cicada.capture.Tracer;
import com.yirendai.infra.cicada.transfer.disruptor.DisruptorTransfer;
import com.yirendai.infra.cicada.transfer.disruptor.SpanEventHandler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferEngine.class);

  private final String url;
  private int sampleRate = 100;
  private int connectTimeout = 100;
  private int soTimeout = 100;
  private int batchSize = 32;
  private int bufferSize = 2 ^ 10;
  private int tpsLimit = 2048;

  public TransferEngine(final String url) {
    this.url = url;
  }

  public void setSampleRate(final int sampleRate) {
    if (sampleRate >= 0) {
      this.sampleRate = sampleRate;
    }
  }

  public void setConnectTimeout(final int connectTimeout) {
    if (connectTimeout > 0) {
      this.connectTimeout = connectTimeout;
    }
  }

  public void setSoTimeout(final int soTimeout) {
    if (soTimeout > 0) {
      this.soTimeout = soTimeout;
    }
  }

  public void setBatchSize(final int batchSize) {
    if (batchSize > 0) {
      this.batchSize = batchSize;
    }
  }

  public void setBufferSize(final int bufferSize) {
    if (bufferSize > 0) {
      this.bufferSize = bufferSize;
    }
  }

  public void setTpsLimit(final int tpsLimit) {
    if (tpsLimit > 0) {
      this.tpsLimit = tpsLimit;
    }
  }

  public void start() {
    if (StringUtils.isBlank(url)) {
      LOGGER.error("url({}) can't be blank!", url);
      throw new java.lang.IllegalArgumentException("transfer url not defined");
    }

    if (sampleRate <= 0 || connectTimeout <= 0 || soTimeout <= 0 || batchSize <= 0 || bufferSize <= 0
        || tpsLimit <= 0) {
      LOGGER.error("sampleRate:{},connectTimeout:{},soTimeout:{},batchSize:{},bufferSize:{},tpsLimit：{} must > 0",
          sampleRate, connectTimeout, soTimeout, batchSize, bufferSize, tpsLimit);
      throw new java.lang.IllegalArgumentException(
          "sampleRate,connectTimeout,soTimeout,batchSize,bufferSize,tpsLimit must > 0");
    }

    final SpanEventHandler eventHandler = new SpanEventHandler(url, connectTimeout, soTimeout, batchSize, tpsLimit);
    final Transfer transfer = new DisruptorTransfer(eventHandler, bufferSize);
    final Tracer tracer = Tracer.getInstance();
    tracer.setSampleRate(sampleRate);
    tracer.setTransfer(transfer);
    CicadaDubboFilter.setTracer(tracer);
  }
}
