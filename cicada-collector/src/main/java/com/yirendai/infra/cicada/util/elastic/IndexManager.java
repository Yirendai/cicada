package com.yirendai.infra.cicada.util.elastic;

import org.springframework.stereotype.Component;

@Component
public interface IndexManager {
  String getCurrentIndexName(String type);
}
