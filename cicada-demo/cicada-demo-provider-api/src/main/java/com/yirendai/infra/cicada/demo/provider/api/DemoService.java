package com.yirendai.infra.cicada.demo.provider.api;

public interface DemoService {
  String sayHello(String name);

  void testResult(String name);

  void testException(String arg);
}
