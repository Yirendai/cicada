package com.yirendai.infra.cicada.capture;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.yirendai.infra.cicada.entity.trace.Endpoint;
import com.yirendai.infra.cicada.entity.trace.Span;
import com.yirendai.infra.cicada.utils.IpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CicadaDubboFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(CicadaDubboFilter.class);

  private static Tracer tracer;

  // 调用过程拦截
  @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.OnlyOneReturn"})
  public Result invoke(final Invoker<?> invoker, final Invocation invocation) throws RpcException {
    if (tracer == null) {
      return invoker.invoke(invocation);
    }
    final long startTime = System.currentTimeMillis();
    final RpcContext context = RpcContext.getContext();

    // 传入参数，暂不做处理
    // Object[] arguments = context.getArguments();
    // for (Object argument : arguments) {
    // LOGGER.error("arg:" + argument);
    // }

    final String localIp = IpUtils.getRealIpWithStaticCache();
    final int localPort = context.getLocalPort();
    final Endpoint endpoint = new Endpoint(localIp, localPort);

    final URL url = context.getUrl();
    final String appName = url.getParameter("application");
    final String serviceName = url.getServiceInterface();
    final String methodName = context.getMethodName();

    final boolean isConsumerSide = context.isConsumerSide();
    Span span = null;
    try {
      if (isConsumerSide) { // 是否是消费者
        final Span parentSpan = tracer.getParentSpan();
        if (parentSpan == null) { // 为rootSpan
          // 生成root Span
          span = tracer.newSpan(appName, serviceName, methodName);
        } else {
          span = tracer.newSpan(appName, serviceName, methodName, parentSpan);
        }
      } else if (context.isProviderSide()) {
        final String traceId = invocation.getAttachment(TracerUtils.TRACE_ID);
        final String parentId = invocation.getAttachment(TracerUtils.PARENT_SPAN_ID);
        final String spanId = invocation.getAttachment(TracerUtils.SPAN_ID);
        final boolean sample = traceId != null;
        span = tracer.genSpan(appName, serviceName, methodName, traceId, parentId, spanId, sample);
      } else {
        LOGGER.error("[" + url + "] [notConsumerNorProvider]");
        return invoker.invoke(invocation);
      }

      invokerBefore(invocation, span, endpoint, startTime);
      final Result result = invoker.invoke(invocation);
      final Throwable throwable = result.getException();
      if (throwable != null && !isConsumerSide) {
        span.addException(serviceName, methodName, throwable, endpoint);
      }

      // 返回值
      // Object resultValue = result.getValue();
      // LOGGER.error("return:" + JSON.toJSONString(resultValue));
      return result;
    } catch (final RpcException ex) {
      if (span != null) {
        span.addException(serviceName, methodName, ex, endpoint);
      }
      throw ex;
    } finally {
      if (span != null) {
        final long end = System.currentTimeMillis();
        invokerAfter(endpoint, span, end, isConsumerSide);// 调用后记录annotation
      }
    }
  }

  private void invokerAfter(final Endpoint endpoint, final Span span, final long end, final boolean isConsumerSide) {
    if (isConsumerSide) {
      tracer.clientReceiveRecord(span, endpoint, end);
    } else {
      tracer.serverSendRecord(span, endpoint, end);
    }
  }

  private void invokerBefore(final Invocation invocation, final Span span, final Endpoint endpoint, final long start) {
    final RpcContext context = RpcContext.getContext();
    if (context.isConsumerSide()) {
      if (span.isSample()) {
        tracer.clientSendRecord(span, endpoint, start);

        final RpcInvocation rpcInvocation = (RpcInvocation) invocation;
        rpcInvocation.setAttachment(TracerUtils.PARENT_SPAN_ID, span.getParentId());
        rpcInvocation.setAttachment(TracerUtils.SPAN_ID, span.getId());
        rpcInvocation.setAttachment(TracerUtils.TRACE_ID, span.getTraceId());
      }
    } else if (context.isProviderSide()) {
      tracer.serverReceiveRecord(span, endpoint, start);
    }
  }

  // setter
  public static void setTracer(final Tracer tra) {
    tracer = tra;
  }

}
