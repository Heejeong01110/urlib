package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.handler.ErrorCode;

public class JwtTokenProcessingException extends AbstractJwtException {


  public JwtTokenProcessingException() {
    super(ErrorCode.TOKEN_PROCESSING_ERROR);
  }
}
