package com.yirendai.infra.cicada.demo.provider.service;

import com.yirendai.infra.cicada.capture.Traceable;
import com.yirendai.infra.cicada.demo.provider.api.DemoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("demoService")
public class DemoServiceImpl implements DemoService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DemoServiceImpl.class);

  @Autowired
  private User user;

  public String sayHello(String name) {
    System.err.println("hhhe:" + name);
    // anotherMethod();
    user.say(3);
    return "Hello " + name;
  }

  public void testResult(String name) {
    anotherMethod();
  }

  @Override
  public void testException(String arg) {
    int aa = 1;
    int bb = 0;
    anotherMethod();
    LOGGER.info("a/b=" + (aa / bb));
  }

  @Traceable
  public void anotherMethod() {
    try {
      TimeUnit.MILLISECONDS.sleep(50);
    } catch (InterruptedException ex) {
      ;
    }
  }
}
