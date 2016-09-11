package com.yirendai.infra.cicada.transfer.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.yirendai.infra.cicada.entity.trace.Span;

public class SpanEventProducer {
  private final RingBuffer<SpanEvent> ringBuffer;

  public SpanEventProducer(final RingBuffer<SpanEvent> ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public void onData(final Span span) {
    final long sequence = ringBuffer.next();
    try {
      final SpanEvent event = ringBuffer.get(sequence);
      event.set(span);
    } finally {
      ringBuffer.publish(sequence);
    }
  }
}
