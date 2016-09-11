package com.yirendai.infra.cicada;

import com.yirendai.infra.cicada.service.LogCollectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationShutdown implements ApplicationListener<ContextClosedEvent> {

  @Autowired
  private LogCollectService collector;

  @Override
  public void onApplicationEvent(final ContextClosedEvent event) {
    collector.finish();
  }
}
