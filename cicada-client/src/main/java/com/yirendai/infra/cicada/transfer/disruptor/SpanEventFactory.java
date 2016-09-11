package com.yirendai.infra.cicada.transfer.disruptor;

import com.lmax.disruptor.EventFactory;

public class SpanEventFactory implements EventFactory<SpanEvent> {
  public SpanEvent newInstance() {
    return new SpanEvent();
  }
}
