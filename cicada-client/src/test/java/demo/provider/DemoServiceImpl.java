package demo.provider;

import com.alibaba.dubbo.rpc.RpcContext;

import demo.provider2.DemoService2;

public class DemoServiceImpl implements DemoService {

  private DemoService2 demoService2;

  public void setDemoService2(final DemoService2 demoService2) {
    this.demoService2 = demoService2;
  }

  public String sayHello(final String name) {
    System.out.println(demoService2.sayWorld(name));
    // System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new
    // Date()) + "] Hello " + name
    // + ", request from consumer: " +
    // RpcContext.getContext().getRemoteAddress());
    return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
  }

}
