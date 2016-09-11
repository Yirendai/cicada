package com.yirendai.infra.cicada.exception;

import com.yirendai.infra.cicada.constants.AppError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BadRequestException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private int errorCode;
  private String message;
  private String data;

  public BadRequestException(final AppError appError) {
    super();
    this.errorCode = appError.getErrorCode();
    this.message = appError.getMessageKey();
  }

  public BadRequestException(final AppError appError, final String data) {
    super();
    this.errorCode = appError.getErrorCode();
    this.message = appError.getMessageKey();
    this.data = data;
  }
}
