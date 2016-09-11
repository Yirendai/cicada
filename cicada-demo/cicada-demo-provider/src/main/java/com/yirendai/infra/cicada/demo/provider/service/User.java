package com.yirendai.infra.cicada.demo.provider.service;

import com.yirendai.infra.cicada.capture.Traceable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "demo.user", ignoreUnknownFields = true)
public class User {
  private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

  private String name = "test";

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Traceable
  public void say(int num) {
    LOGGER.info("hi, boy:the num is:" + num);

    num--;

    if (num > 0) {
      say(num);
    }
  }

  @Traceable
  public String toString() {
    return name;
  }
}
