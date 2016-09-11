package com.yirendai.infra.cicada;

import com.alibaba.dubbo.remoting.ExecutionException;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;

public final class ExceptionTest {
  private ExceptionTest(){}

  public static void main(final String... args) {
    final ExecutionException ee = new ExecutionException(null, null, "hee");

    System.err.println("ee:" + JSON.toJSONString(ee));
    System.err.println("em:" + ee.getMessage());
    System.err.println("em-1:" + JSON.toJSONString(ee.getClass()));
    System.err.println("em-2:" + ee.getClass().getCanonicalName());

    final RpcException re = new RpcException("sx");
    System.err.println("re:" + JSON.toJSONString(re));
    System.err.println("rem:" + re.getMessage());

    final TimeoutException te = new TimeoutException(true, null, "sss");
    System.err.println("te:" + JSON.toJSONString(te));

    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    final Throwable tb = new NullPointerException("null");
    System.err.println("tb:" + JSON.toJSONString(tb));
  }
}
