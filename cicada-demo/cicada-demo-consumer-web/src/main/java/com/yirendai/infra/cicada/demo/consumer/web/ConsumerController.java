package com.yirendai.infra.cicada.demo.consumer.web;

import com.yirendai.infra.cicada.demo.provider.api.DemoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

@RestController
@SpringBootApplication(scanBasePackages={"com.yirendai.infra.cicada"})
@ServletComponentScan(basePackages={"com.yirendai.infra.cicada"})
public class ConsumerController {

  @Autowired
  private DemoService demoService;
  
  @RequestMapping("/hi")
  String home() {
      return "Hello World!";
  }

  @RequestMapping(value = "/testAction", method = RequestMethod.GET)
  @ResponseBody
  public String allOnActivities() throws UnsupportedEncodingException {
    asleepMethod();
    String helloStr = demoService.sayHello("testAction");
    return helloStr;
  }

  @RequestMapping(value = "/test", method = RequestMethod.GET)
  @ResponseBody
  public String test() throws UnsupportedEncodingException {
    asleepMethod();
    String helloStr = demoService.sayHello("test");
    return helloStr;
  }

  @RequestMapping(value = "/testException", method = RequestMethod.GET)
  @ResponseBody
  public String testException() throws UnsupportedEncodingException {
    asleepMethod();
    demoService.testException("myException");
    return "succ";
  }

  private void asleepMethod() {
    try {
      TimeUnit.MILLISECONDS.sleep(50);
    } catch (InterruptedException ex) {
      ;
    }
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ConsumerController.class, args);
  }
}
