package demo.consumer;

import demo.provider.DemoService;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Random;

public final class DemoConsumer {

  private DemoConsumer() {}

  @SuppressWarnings("PMD.AvoidPrintStackTrace")
  public static void main(final String... args) throws Exception {

    try {
      final ClassPathXmlApplicationContext context =
          new ClassPathXmlApplicationContext(new String[] {"dubbo-demo-consumer.xml", "/cicada-config.xml"});
      context.start();
      final DemoService service = (DemoService) context.getBean("demoService"); // 获取远程服务代理

      final Random random = new Random();
      int totalNum = 1000000;
      while (totalNum > 0) {
        final String str = service.sayHello("hello~");
        System.out.println("message:" + str);
        final long time = 0 + random.nextInt(2000);
        Thread.sleep(time);
        totalNum--;
      }

      context.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
