package com.yirendai.infra.cicada.capture;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Traceable {
  /**
   * TRACE level of logging.
   */
  @SuppressWarnings("PMD.RedundantFieldInitializer")
  int TRACE = 0;

  /**
   * DEBUG level of logging.
   */
  int DEBUG = 1;

  /**
   * INFO level of logging.
   */
  int INFO = 2;

  /**
   * WARN level of logging.
   */
  int WARN = 3;

  /**
   * ERROR level of logging.
   */
  int ERROR = 4;

  /**
   * Level of logging.
   */
  int value() default Traceable.INFO;
}
