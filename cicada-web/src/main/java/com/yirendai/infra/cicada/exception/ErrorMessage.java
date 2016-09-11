package com.yirendai.infra.cicada.exception;

import lombok.Data;

@Data
public class ErrorMessage {

  private long timestamp;
  private int code;
  private String message;
  private String data;

  public ErrorMessage() {
    super();
  }

  public ErrorMessage(final int code, final String message) {
    this.code = code;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
  }

  public ErrorMessage(final int code, final String message, final String data) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.timestamp = System.currentTimeMillis();
  }
}
