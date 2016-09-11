package com.yirendai.infra.cicada.utils;

import com.alibaba.dubbo.config.ApplicationConfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextUtil implements ApplicationContextAware {
  private static ApplicationContext applicationContext; // Spring应用上下文环境

  /**
   * 实现ApplicationContextAware接口的回调方法，设置上下文环境.
   * 
   */
  public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    SpringContextUtil.applicationContext = applicationContext;
  }

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }




  /**
   * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true.
   * 
   */
  public static boolean containsBean(final String name) {
    return applicationContext.containsBean(name);
  }

  /**
   * 判断以给定名字注册的bean定义是一个singleton还是一个prototype.
   * 
   * @throws NoSuchBeanDefinitionException - 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常
   */
  public static boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
    return applicationContext.isSingleton(name);
  }

  /**
   * @return Class 注册对象的类型.
   */
  public static Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
    return applicationContext.getType(name);
  }

  /**
   * 如果给定的bean名字在bean定义中有别名，则返回这些别名.
   * 
   */
  public static String[] getAliases(final String name) throws NoSuchBeanDefinitionException {
    return applicationContext.getAliases(name);
  }
  
  /**
   * 获取对象.
   * 
   */
  public static Object getBean(final String name) throws BeansException {
    return applicationContext.getBean(name);
  }
  
  /**
   * 获取类型为requiredType的对象 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）.
   * 
   * @param name bean注册名
   * @param requiredType 返回对象类型
   * @return Object 返回requiredType类型对象
   */
  public static <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
    return applicationContext.getBean(name, requiredType);
  }

  /**
   * 获取对象.
   * 
   * @return Object 一个以所给名字注册的bean的实例
   */
  public static <T> T getBean(final Class<T> clz) throws BeansException {
    return applicationContext.getBean(clz);
  }

  /**
   * 获取对象.
   * 
   * @return Object 一个以所给名字注册的bean的实例
   */
  public static <T> T getBean(final Class<T> clz, final T defaultValue) {
    T ret = null;
    try {
      ret = applicationContext.getBean(clz);
    } catch (BeansException ex) {
      ret = defaultValue;
    }

    return ret;
  }

  public static String getAppName(final String defaultValue) {
    String retValue = null;
    final ApplicationConfig application = getBean(ApplicationConfig.class, null);
    if (application != null) {
      retValue = application.getName();
    }

    if (StringUtils.isBlank(retValue) && applicationContext != null) {
      retValue = applicationContext.getApplicationName();
    }

    if (StringUtils.isBlank(retValue)) {
      retValue = defaultValue;
    }

    return retValue;
  }
}
