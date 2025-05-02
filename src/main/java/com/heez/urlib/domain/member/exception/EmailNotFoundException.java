package com.heez.urlib.domain.member.exception;

import com.heez.urlib.global.error.response.ErrorCode;

public class EmailNotFoundException extends RuntimeException {

  private final ErrorCode errorCode;

  public EmailNotFoundException(final ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
