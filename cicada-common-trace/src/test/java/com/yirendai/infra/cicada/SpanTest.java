package com.yirendai.infra.cicada;

import com.yirendai.infra.cicada.entity.trace.Span;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpanTest {
  public static void main(String[] args) {
    List<Span> list = new ArrayList<Span>();

    Span span2 = new Span();
    span2.setTraceId("1");
    span2.setId("1.1");
    list.add(span2);

    Span span4 = new Span();
    span4.setTraceId("1");
    span4.setId("1.1.1");
    list.add(span4);

    Span span3 = new Span();
    span3.setTraceId("1");
    span3.setId("1.13");
    list.add(span3);

    Span span1 = new Span();
    span1.setTraceId("1");
    span1.setId("1");
    list.add(span1);

    Collections.sort(list);

    for (Span span : list) {
      System.err.println("span:" + span);
    }

    System.err.println("=============================\n");

    span1.setTraceId("1.2");
    span2.setTraceId("1.1");
    span3.setTraceId("1.1.13");
    span4.setTraceId("1.1.1");
    Collections.sort(list);

    for (Span span : list) {
      System.err.println("span:" + span);
    }
  }
}
