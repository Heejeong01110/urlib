package com.heez.urlib.global.error.exception;

import com.heez.urlib.global.error.handler.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public abstract class AbstractJwtException extends AuthenticationException implements
    JwtExceptionType {

  private final ErrorCode errorCode;

  protected AbstractJwtException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  @Override
  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
