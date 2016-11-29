package com.yirendai.infra.cicada.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.yirendai.infra.cicada.entity.model.AnnotationModel;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.entity.trace.Annotation;
import com.yirendai.infra.cicada.entity.trace.BinaryAnnotation;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.repository.LogReader;
import com.yirendai.infra.cicada.util.TraceChainModelGenerator;
import com.yirendai.infra.cicada.util.progress.MessageWasher;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LogCollectService {
  @Autowired
  private LogReader reader;

  @Autowired
  private MessageWasher washer;

  @Autowired
  private TraceChainModelGenerator generator;

  @Autowired
  private ElasticUploadService uploadTask;

  private volatile boolean isInterrupt = false;

  // 执行清理过程
  public void finish() {
    isInterrupt = true;
    reader.finish();
  }

  // 启动任务，循环读取日志内容写入ES
  public void start() {
    while (true) {
      if (isInterrupt) {
        break;
      }
      process();
    }
  }

  public void process() {
    // 读取所有日志信息
    final List<String> lines = reader.read();
    if (lines.isEmpty()) {
      try {
        TimeUnit.MILLISECONDS.sleep(1000);
      } catch (InterruptedException ex) {
        log.error("failed sleep interrupted, error{}", ex);
      }
      return;
    }

    // 解析日志数据，生成调用链的数据信息
    final List<SpanModel> spanModels = new LinkedList<SpanModel>();
    final List<AnnotationModel> annoModels = new LinkedList<AnnotationModel>();
    analyze(lines, spanModels, annoModels);

    // 异步上传数据到ES
    uploadTask.upload(spanModels, annoModels);
  }

  /**
   * 解析日志行.
   * 
   * @param lines 输入参数，从日志中读取到的原始采集数据
   * @param spanModels 输出参数，处理之后的SpanModel数据
   * @param annoModels 输出参数，处理之后的AnnotationModel数据
   */
  private void analyze(final List<String> lines, //
      final List<SpanModel> spanModels, //
      final List<AnnotationModel> annoModels) {

    String message = null;
    for (final String line : lines) {
      try {
        // 清洗数据
        message = washer.wash(line);
        // 解json
        final List<Span> spans = JSON.parseObject(message, new TypeReference<List<Span>>() {});
        // 生成调用链数据的存储对象
        genTraceChainModel(spans, spanModels, annoModels);
      } catch (JSONException ex) {
        log.error("failed parse line {}, error {}", message, ex);
        continue;
      }
    }
  }

  /**
   * 根据日志信息生成调用链数据的存储对象(SpanModel/AnnotationModel).
   */
  private void genTraceChainModel(final List<Span> spans, 
      final List<SpanModel> spanModels, 
      final List<AnnotationModel> annoModels) {

    // 生成span、annotation存储对象
    for (final Span span : spans) {
      // 清洗annotation和binaryAnnotation信息，生成AnnotationModel信息
      for (final Annotation anno : span.getAnnotations()) {
        final AnnotationModel annoModel = generator.genAnnotationModel(span, anno);
        annoModels.add(annoModel);
      }

      for (final BinaryAnnotation binAnno : span.getBinaryAnnotations()) {
        final AnnotationModel annoModel = generator.genAnnotationModel(span, binAnno);
        annoModels.add(annoModel);
      }

      // 如果是consumer端生成的span信息，继续下一循环
      if (span.isConsumerSide()) {
        continue;
      }

      // 生成SpanModel信息
      final SpanModel spanModel = generator.genSpanModel(span);
      if (spanModel != null) {
        spanModels.add(spanModel);
      }
    }
  }
}
