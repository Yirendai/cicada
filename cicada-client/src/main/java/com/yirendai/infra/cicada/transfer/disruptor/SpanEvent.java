package com.yirendai.infra.cicada.transfer.disruptor;

import com.yirendai.infra.cicada.entity.trace.Span;

public class SpanEvent {
  private Span span;

  public void set(final Span span) {
    this.span = span;
  }

  public Span get() {
    return this.span;
  }
}
