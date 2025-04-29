package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.response.ErrorCode;
import lombok.Getter;

@Getter
public class TokenIsLogoutException extends RuntimeException {

  private final ErrorCode errorCode;

  public TokenIsLogoutException(final ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

}
