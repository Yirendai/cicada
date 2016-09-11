package com.yirendai.infra.cicada.capture;

import com.alibaba.dubbo.common.utils.StringUtils;

final class TracerUtils {
  public static final String TRACE_ID = "traceId";
  public static final String SPAN_ID = "spanId";
  public static final String PARENT_SPAN_ID = "parentId";
  public static final String SAMPLE = "isSample";

  private TracerUtils() {}

  public static Long getAttachmentLong(final String value) {
    Long retLong = null;
    if (!StringUtils.isBlank(value)) {
      retLong = Long.valueOf(value);
    }

    return retLong;
  }

  public static Boolean getAttachmentBoolean(final String value) {
    Boolean ret = Boolean.FALSE;
    if (value != null) {
      ret = Boolean.valueOf(value);
    }

    return ret;
  }

  public static Integer getAttachmentInteger(final String value) {
    Integer ret = null;
    if (!StringUtils.isBlank(value)) {
      ret = Integer.valueOf(value);
    }
    
    return ret;
  }

  public static int getAttachmentInt(final String value) {
    int ret = 0;
    if (!StringUtils.isBlank(value)) {
      ret = Integer.valueOf(value);
    }
    
    return ret;
  }
}
