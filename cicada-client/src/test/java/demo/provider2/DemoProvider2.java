package demo.provider2;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class DemoProvider2 {
  
  private DemoProvider2(){}
  
  public static void main(final String... args) throws Exception {
    final ClassPathXmlApplicationContext context =
        new ClassPathXmlApplicationContext(new String[] {"dubbo-demo-provider2.xml", "/cicada-config.xml"});
    context.start();
    System.in.read(); // 按任意键退出
    context.close();
  }
}
