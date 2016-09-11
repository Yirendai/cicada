package com.yirendai.infra.cicada.utils;

import com.yirendai.infra.cicada.constants.AnnotationType;
import com.yirendai.infra.cicada.entity.model.SpanModel;
import com.yirendai.infra.cicada.entity.trace.Annotation;
import com.yirendai.infra.cicada.entity.trace.Span;

import java.util.List;

public final class TraceUtil {
  private TraceUtil() { }
  
  public static boolean isRootSpan(final Span span) {
    return span.getParentId() == null;
  }

  public static boolean isRootSpanModel(final SpanModel span) {
    return span.getParentId() == null;
  }

  public static int getTraceDuration(final Span span) {
    final List<Annotation> annotations = span.getAnnotations();
    final Annotation cr = getCrAnnotation(annotations);
    final Annotation cs = getCsAnnotation(annotations);
    
    final int duration;
    if (cs == null || cr == null) {
      duration = 0;
    } else {
      duration = (int) (cr.getTimestamp() - cs.getTimestamp());
    }

    return duration;
  }

  /**
   * 获取span的durationServer时间.
   * 
   * @param span a Span instance
   * @return server duration millis
   */
  public static int calcSpanDurationServer(final Span span) {
    Long sr = null;
    Long ss = null;
    for (final Annotation anno : span.getAnnotations()) {
      if (anno.getType() == AnnotationType.SERVER_SEND) {
        ss = anno.getTimestamp();
      }

      if (anno.getType() == AnnotationType.SERVER_RECEIVE) {
        sr = anno.getTimestamp();
      }
    }
    
    final int duration;
    if (ss == null || sr == null) {
      duration = -1;
    } else {
      duration = (int) (ss - sr);
    }
    
    return duration;
  }

  /**
   * 获取span的durationServer时间.
   */
  public static int calcSpanDurationClient(final Span span) {
    Long cr = null;
    Long cs = null;
    for (final Annotation anno : span.getAnnotations()) {
      if (anno.getType() == AnnotationType.CLIENT_SEND) {
        cs = anno.getTimestamp();
      }

      if (anno.getType() == AnnotationType.CLIENT_RECEIVE) {
        cr = anno.getTimestamp();
      }
    }

    int duration = -1;
    if (cs != null && cr != null) {
      duration = (int) (cr - cs);
    }
    return duration;
  }

  public static void genSpanDuration(final Span span) {
    Long sr = null;
    Long ss = null;
    Long cr = null;
    Long cs = null;
    for (final Annotation anno : span.getAnnotations()) {
      if (anno.getType() == AnnotationType.SERVER_SEND) {
        ss = anno.getTimestamp();
      }

      if (anno.getType() == AnnotationType.SERVER_RECEIVE) {
        sr = anno.getTimestamp();
      }

      if (anno.getType() == AnnotationType.CLIENT_SEND) {
        cs = anno.getTimestamp();
      }

      if (anno.getType() == AnnotationType.CLIENT_RECEIVE) {
        cr = anno.getTimestamp();
      }
    }

    if (sr != null && ss != null) {
      span.setDurationServer((int) (ss - sr));
    }

    if (cr != null && cs != null) {
      span.setDurationClient((int) (cr - cs));
    }
  }

  public static Annotation getCsAnnotation(final List<Annotation> annotations) {
    Annotation result = null;
    for (final Annotation a : annotations) {
      if (AnnotationType.CLIENT_SEND == a.getType()) {
        result = a;
        break;
      }
    }
    return result;
  }

  public static Annotation getCrAnnotation(final List<Annotation> annotations) {
    Annotation result = null;
    for (final Annotation a : annotations) {
      if (AnnotationType.CLIENT_RECEIVE == a.getType()) {
        result = a;
        break;
      }
    }
    
    return result;
  }

  public static Annotation getSsAnnotation(final List<Annotation> annotations) {
    Annotation result = null;
    for (final Annotation a : annotations) {
      if (AnnotationType.SERVER_SEND == a.getType()) {
        result = a;
        break;
      }
    }
    
    return result;
  }

  public static Annotation getSrAnnotation(final List<Annotation> annotations) {
    Annotation result = null;
    for (final Annotation a : annotations) {
      if (AnnotationType.SERVER_RECEIVE == a.getType()) {
        result = a;
        break;
      }
    }

    return result;
  }
}
