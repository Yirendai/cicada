package com.yirendai.infra.cicada.transfer;

import com.yirendai.infra.cicada.entity.trace.Span;

import java.util.List;

public interface DeliverService {
  boolean deliver(Span span);

  boolean deliver(List<Span> spanList);
}
