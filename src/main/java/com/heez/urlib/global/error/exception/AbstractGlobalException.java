package com.heez.urlib.global.error.exception;

import com.heez.urlib.global.error.handler.ErrorCode;

public abstract class AbstractGlobalException extends RuntimeException {

  private final ErrorCode errorCode;

  protected AbstractGlobalException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
