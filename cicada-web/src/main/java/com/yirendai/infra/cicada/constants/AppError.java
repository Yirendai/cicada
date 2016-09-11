package com.yirendai.infra.cicada.constants;

/**
 * 错误信息定义.
 * @author zeche
 */
public enum AppError {

  INCOMPLETE_PAGE_REQUEST_PARAMS(2001, "page.request.params.incomplete"),

  OTHER_METHOD_ARGS_NOT_VALID(9000, ""), OTHER_HTTP_MEDIATYPE_NOT_SUPPORT(9001,
      "other.contenttype.unsupport"), OTHER_HTTP_MESSAGE_NOT_READABLE(9002,
          "other.message.not.readable"), OTHER_HTTP_TYPE_MISMATCH(9003,
              "other.type.mismatch"), OTHER_SERVER_INERNAL_EXCEPTION(9999, "other.server.internal.error");

  private int errorCode;
  private String messageKey;

  AppError(final int code, final String messageKey) {
    this.errorCode = code;
    this.messageKey = messageKey;
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  public String getMessageKey() {
    return this.messageKey;
  }

  public static AppError valueOf(final int errorCode) {
    AppError appError = null;
    for (final AppError error : values()) {
      if (error.getErrorCode() == errorCode) {
        appError = error;
        break;
      }
    }

    return appError;
  }
}
