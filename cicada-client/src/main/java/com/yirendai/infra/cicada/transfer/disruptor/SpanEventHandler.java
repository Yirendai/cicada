package com.yirendai.infra.cicada.transfer.disruptor;

import com.lmax.disruptor.EventHandler;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.transfer.DeliverService;
import com.yirendai.infra.cicada.transfer.impl.HttpPostDeliverService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SpanEventHandler implements EventHandler<SpanEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpanEventHandler.class);

  private final List<Span> spanList = new ArrayList<Span>();

  private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
  private static final int DEFAULT_SO_TIMEOUT = 5000;
  private static final int DEFAULT_BATCH_SIZE = 32;
  private static final int DEFAULT_TPS_LIMIT = 2048;

  private DeliverService transferService;
  private int batchSize = DEFAULT_BATCH_SIZE;
  private int tpsLimit = DEFAULT_TPS_LIMIT;

  private long lastRecordTime;
  private int spanNum;
  private int dropNum;

  public SpanEventHandler(final String url) {
    this(url, DEFAULT_CONNECT_TIMEOUT, DEFAULT_SO_TIMEOUT, DEFAULT_BATCH_SIZE, DEFAULT_TPS_LIMIT);
    lastRecordTime = System.currentTimeMillis() / 1000;
  }

  public SpanEventHandler(final String url, final int connectTimeout, final int soTimeout, final int batchSize,
      final int tpsLimit) {
    if (StringUtils.isBlank(url)) {
      LOGGER.error("url shoud not empty!");
      throw new IllegalArgumentException("url shoud not empty!");
    }

    int finConnectTimeout = connectTimeout;
    int finSoTimeout = soTimeout;

    if (finConnectTimeout <= 0) {
      finConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
    }

    if (finSoTimeout <= 0) {
      finSoTimeout = DEFAULT_SO_TIMEOUT;
    }

    transferService = new HttpPostDeliverService(url, finConnectTimeout, finSoTimeout);

    if (batchSize > 0) {
      this.batchSize = batchSize;
    } else {
      this.batchSize = DEFAULT_BATCH_SIZE;
    }

    this.tpsLimit = tpsLimit;
    if (this.tpsLimit <= 0) {
      this.tpsLimit = DEFAULT_TPS_LIMIT;
    }
  }

  public void onEvent(final SpanEvent spanEvent, final long sequence, final boolean endOfBatch) throws Exception {
    // LOGGER.info("receiveMsg:" + JSON.toJSONString(spanEvent.get()));

    final long currentTime = System.currentTimeMillis() / 1000;

    if (currentTime != lastRecordTime) {
      lastRecordTime = currentTime;
      spanNum = 1;
      if (dropNum > 0) {
        LOGGER.warn("too fast,drop some message!{}", dropNum);
        dropNum = 0;
      }
    } else {
      spanNum++;
      if (spanNum > tpsLimit) {
        dropNum++;
        return;
      }
    }

    try {
      final Span span = spanEvent.get();
      spanList.add(span);

      if (endOfBatch || spanList.size() >= batchSize) {
        transferService.deliver(spanList);
        spanList.clear();
      }

    } catch (Exception ex) {
      LOGGER.error("CreateHttpConnection error", ex);
    }
  }

}
