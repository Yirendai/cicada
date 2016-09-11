package com.yirendai.infra.cicada;

import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.transfer.Transfer;
import com.yirendai.infra.cicada.transfer.disruptor.DisruptorTransfer;
import com.yirendai.infra.cicada.transfer.disruptor.SpanEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 压测工具，可以用来测试日志传送的TPS大小，协助设置TPS最大值.
 *
 * @author zpc
 */
public final class BenchmarkTransfer {
  private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkTransfer.class);

  private BenchmarkTransfer() {}

  @SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidInstantiatingObjectsInLoops"})
  public static void main(final String... args) {
    if (args.length <= 0) {
      System.err.println("java BenchmarkTransfer <TestURL>");
      return;
    }

    final String url = args[0];
    final int connectTimeout = 100;
    final int soTimeout = 100;
    final int batchSize = 32;
    final int bufferSize = 2 ^ 10;
    final int tpsLimit = 2048;
    final SpanEventHandler eventHandler = new SpanEventHandler(url, connectTimeout, soTimeout, batchSize, tpsLimit);
    final Transfer transfer = new DisruptorTransfer(eventHandler, bufferSize);

    final long startTime = System.currentTimeMillis();
    final AtomicLong num = new AtomicLong(0);

    for (int i = 0; i < 11; i++) {
      new Thread(new Runnable() {
        public void run() {
          while (true) {
            try {
              num.compareAndSet(Integer.MAX_VALUE, 0);
              final long id = num.incrementAndGet();
              final Span span = new Span();
              span.setTraceId(Long.toString(id));
              transfer.asyncSend(span);

              TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception ex) {
              LOGGER.error("error!", ex);
            }
          }
        }
      }, "productThread-" + i).start();
    }

    new Thread(new Runnable() {
      public void run() {
        while (true) {
          try {
            TimeUnit.SECONDS.sleep(1);
          } catch (InterruptedException ex) {
            LOGGER.error("error!", ex);
          }
          final long duration = System.currentTimeMillis() - startTime;
          LOGGER.error("TPS:" + (num.get() * 1000 / duration));
        }
      }

    }, "staticsThead").start();

  }
}
