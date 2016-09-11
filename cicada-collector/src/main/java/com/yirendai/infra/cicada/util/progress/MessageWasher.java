package com.yirendai.infra.cicada.util.progress;

import org.springframework.stereotype.Component;

@Component
public class MessageWasher {

  public String wash(String message) {
    return message.replace("\\x22", "\"");
  }
}
