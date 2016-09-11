package com.yirendai.infra.cicada.demo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.yirendai.infra.cicada"})
public class Provider {
//  private static final Logger LOG = LoggerFactory.getLogger(Provider.class);

//  @RequestMapping("/h2")
//  String home() {
//      return "Hello World!";
//  }
//  
  public static void main(String[] args) throws Exception {
    SpringApplication.run(Provider.class, args);
   
   /// System.in.read();
  }

}
