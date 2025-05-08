package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.response.ErrorCode;

public class InvalidJwtFormatException extends AbstractJwtException {

  public InvalidJwtFormatException() {
    super(ErrorCode.INVALID_TOKEN_FORMAT);
  }
}
