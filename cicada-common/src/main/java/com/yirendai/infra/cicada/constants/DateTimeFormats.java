package com.yirendai.infra.cicada.constants;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeFormats {

  public static final String FULL_DATE_CHINESE = "yyyy年MM月dd日";
  public static final DateTimeFormatter FULL_DATE_CHINESE_FORMATTER =
      DateTimeFormat.forPattern(DateTimeFormats.FULL_DATE_CHINESE);

  public static final String HALF_DATE_CHINESE = "MM月dd日";
  public static final DateTimeFormatter HALF_DATE_CHINESE_FORMATTER =
      DateTimeFormat.forPattern(DateTimeFormats.HALF_DATE_CHINESE);

  public static final String FULL_DATE_ENGLISH = "yyyy-MM-dd";
  public static final DateTimeFormatter FULL_DATE_ENGLISH_FORMATTER =
      DateTimeFormat.forPattern(DateTimeFormats.FULL_DATE_ENGLISH);

  public static final String FULL_TIME_ENGLISH = "yyyy-MM-dd HH:mm:ss";
  public static final DateTimeFormatter FULL_TIME_ENGLISH_FORMATTER =
      DateTimeFormat.forPattern(DateTimeFormats.FULL_TIME_ENGLISH);

  public static final String FULL_TIME_COMPACT_ENGLISH = "yyyyMMddHHmmss";
  public static final DateTimeFormatter FULL_TIME_COMPACT_ENGLISH_FORMATTER =
      DateTimeFormat.forPattern(DateTimeFormats.FULL_TIME_COMPACT_ENGLISH);

  public static final String CHINESE_TIME_ZONE = "GMT+8";
}
