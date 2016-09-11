package com.yirendai.infra.cicada.capture;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MybatisInterceptor implements Interceptor {

  public Object intercept(final Invocation invocation) throws Throwable {
    final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
    Object returnValue = null;
    final long start = System.currentTimeMillis();
    returnValue = invocation.proceed();
    final long end = System.currentTimeMillis();

    final String sqlId = mappedStatement.getId();
    final int lastIndex = sqlId.lastIndexOf('.');
    final String className = sqlId.substring(0, lastIndex);
    final String methodName = sqlId.substring(lastIndex + 1);
    Tracer.getInstance().addBinaryAnnotation(className, methodName, (int) (end - start));

    return returnValue;
  }

  public Object plugin(final Object target) {
    return Plugin.wrap(target, this);
  }

  public void setProperties(final Properties properties0) {
    //do nothing
  }
}
