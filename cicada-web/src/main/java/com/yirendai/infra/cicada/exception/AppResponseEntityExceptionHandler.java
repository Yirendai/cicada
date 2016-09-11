package com.yirendai.infra.cicada.exception;

import com.jcabi.aspects.Loggable;
import com.yirendai.infra.cicada.constants.AppError;
import com.yirendai.infra.cicada.util.Messages;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@Loggable
@ControllerAdvice
public class AppResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
  @Autowired
  Messages messages;

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, //
      final HttpHeaders headers, //
      final HttpStatus status, //
      final WebRequest request) {

    log.error("MethodArgumentNotValidException:{}", ex);
    final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    final ErrorMessage errorMessage =
        new ErrorMessage(AppError.OTHER_METHOD_ARGS_NOT_VALID.getErrorCode(), fieldErrors.get(0).getDefaultMessage());
    return new ResponseEntity<Object>(errorMessage, headers, status);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, //
      final HttpHeaders headers, //
      final HttpStatus status, //
      final WebRequest request) {
    log.error("HttpMediaTypeNotSupportedException:{}", ex);
    final String message = messages.get(AppError.OTHER_HTTP_MEDIATYPE_NOT_SUPPORT.getMessageKey(), //
        ex.getContentType().getType(), //
        MediaType.toString(ex.getSupportedMediaTypes()));
    final ErrorMessage errorMessage = //
        new ErrorMessage(AppError.OTHER_HTTP_MEDIATYPE_NOT_SUPPORT.getErrorCode(), message);
    return new ResponseEntity<Object>(errorMessage, headers, status);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, //
      final HttpHeaders headers, //
      final HttpStatus status, //
      final WebRequest request) {
    log.error("HttpMessageNotReadableException:{}", ex);

    final AppError appError = AppError.OTHER_HTTP_MESSAGE_NOT_READABLE;
    final ErrorMessage errorMessage = new ErrorMessage(appError.getErrorCode(), appError.getMessageKey());
    return new ResponseEntity<Object>(errorMessage, headers, status);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, //
      final HttpHeaders headers, //
      final HttpStatus status, //
      final WebRequest request) {
    log.error("handleTypeMismatch:{}", ex);

    final ErrorMessage errorMessage =  //
        new ErrorMessage(AppError.OTHER_HTTP_TYPE_MISMATCH.getErrorCode(), "params.type.mismatch");
    return new ResponseEntity<Object>(errorMessage, headers, status);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, //
      final Object body, //
      final HttpHeaders headers, //
      final HttpStatus status, //
      final WebRequest request) {

    log.error("{}:{}", ex.getClass(), ex);
    final ErrorMessage errorMessage = //
        new ErrorMessage(AppError.OTHER_SERVER_INERNAL_EXCEPTION.getErrorCode(), ex.getMessage());
    return new ResponseEntity<Object>(errorMessage, headers, status);

  }

  @ExceptionHandler(value = {BadRequestException.class})
  protected ResponseEntity<Object> handleBadRequestException(final BadRequestException ex, //
      final WebRequest request) {

    log.error("{}", ex.getMessage());
    final HttpStatus status = HttpStatus.BAD_REQUEST;
    final ErrorMessage errorMessage = new ErrorMessage(ex.getErrorCode(), ex.getMessage(), ex.getData());
    return new ResponseEntity<Object>(errorMessage, status);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  protected ResponseEntity<Object> handleNotFoundException(final NotFoundException ex, final WebRequest request) {

    log.error("{}", ex.getMessage());
    final HttpStatus status = HttpStatus.NOT_FOUND;
    final ErrorMessage errorMessage = new ErrorMessage(ex.getErrorCode(), ex.getMessage());
    return new ResponseEntity<Object>(errorMessage, status);
  }

  @ExceptionHandler(value = {InternalServerErrorException.class})
  protected ResponseEntity<Object> handleInternalServerException(final Exception ex, final WebRequest request) {

    log.error("handleInternalServerException {}:{}", ex.getClass(), ex);
    final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    final ErrorMessage errorMessage = //
        new ErrorMessage(AppError.OTHER_SERVER_INERNAL_EXCEPTION.getErrorCode(), ex.getMessage());
    return new ResponseEntity<Object>(errorMessage, status);
  }

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleGenericException(final Exception ex, final WebRequest request) {

    log.error("handleGenericException {}:{}", ex.getClass(), ex);
    final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    final ErrorMessage errorMessage = //
        new ErrorMessage(AppError.OTHER_SERVER_INERNAL_EXCEPTION.getErrorCode(), ex.getMessage());
    return new ResponseEntity<Object>(errorMessage, status);
  }
}
