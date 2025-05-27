package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.handler.ErrorCode;

public class UnsupportedJwtTokenException extends AbstractJwtException {

  public UnsupportedJwtTokenException() {
    super(ErrorCode.TOKEN_PROCESSING_ERROR);
  }
}
