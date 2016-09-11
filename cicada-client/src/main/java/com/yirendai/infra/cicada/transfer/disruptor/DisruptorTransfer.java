package com.yirendai.infra.cicada.transfer.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.transfer.Transfer;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public class DisruptorTransfer implements Transfer {

  private static final int DEFAULT_BUFFER_SIZE = 1024;

  private final AtomicBoolean ready = new AtomicBoolean(false);

  private Disruptor<SpanEvent> disruptor;

  private SpanEventProducer producer;

  public DisruptorTransfer(final SpanEventHandler spanEventHandler) {
    this(spanEventHandler, DEFAULT_BUFFER_SIZE);
  }

  @SuppressWarnings("unchecked")
  public DisruptorTransfer(final SpanEventHandler spanEventHandler, final int buffSize) {
    // Executor executor = Executors.newCachedThreadPool();
    final ThreadFactory threadFactory = Executors.defaultThreadFactory();

    // The factory for the event
    final SpanEventFactory factory = new SpanEventFactory();

    // Specify the size of the ring buffer, must be power of 2.
    final int bufferSize = buffSize;

    // Construct the Disruptor
    disruptor = new Disruptor<SpanEvent>(factory, bufferSize, threadFactory);

    // Connect the handler
    // disruptor.handleEventsWith(new
    // SpanEventHandler("http://localhost:9080/upload"));
    disruptor.handleEventsWith(spanEventHandler);

    // Start the Disruptor, starts all threads running
    disruptor.start();

    final RingBuffer<SpanEvent> ringBuffer = disruptor.getRingBuffer();
    producer = new SpanEventProducer(ringBuffer);
  }

  public boolean isReady() {
    return ready.get();
  }

  public boolean isServiceReady(final String serviceName) {
    return ready.get();
  }

  public void start() throws Exception {
    // do nothing
  }

  public void cancel() {
    disruptor.shutdown();
  }

  public void asyncSend(final Span span) {
    producer.onData(span);
  }

}
