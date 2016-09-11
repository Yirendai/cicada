package com.yirendai.infra.cicada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
  public static void main(final String... args) {
    SpringApplication.run(Application.class, args);
  }
}
