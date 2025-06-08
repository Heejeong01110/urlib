package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractJwtException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class InvalidJwtFormatException extends AbstractJwtException {

  public InvalidJwtFormatException() {
    super(ErrorCode.INVALID_TOKEN_FORMAT);
  }
}
