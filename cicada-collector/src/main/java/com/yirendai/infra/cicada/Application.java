package com.yirendai.infra.cicada;

import com.yirendai.infra.cicada.service.LogCollectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {
  
  @Autowired
  private LogCollectService collector;
  
  private void run() {
    this.collector.start();
  }
  
  public static void main(final String... args) {
    final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    context.getBean(Application.class).run();
  }
}
