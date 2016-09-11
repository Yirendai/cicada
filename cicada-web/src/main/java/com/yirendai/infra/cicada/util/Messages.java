package com.yirendai.infra.cicada.util;

import com.yirendai.infra.cicada.constants.AppError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Messages {

  @Autowired
  MessageSource messageSource;

  public String get(final String code) {
    return this.get(code, Locale.getDefault(), new String[] {});
  }

  public String get(final String code, final String arg) {
    return this.get(code, Locale.getDefault(), new String[] {arg});
  }

  public String get(final String code, final String... args) {
    return this.get(code, Locale.getDefault(), args);
  }

  public String get(final String code, final Locale locale, final String... args) {
    return messageSource.getMessage(code, args, locale);
  }

  public String get(final AppError appError, final String... args) {
    return this.get(appError.getMessageKey(), Locale.getDefault(), args);
  }
}
