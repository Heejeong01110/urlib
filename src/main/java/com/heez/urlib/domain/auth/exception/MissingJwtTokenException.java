package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractJwtException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class MissingJwtTokenException extends AbstractJwtException {

  public MissingJwtTokenException() {
    super(ErrorCode.MISSING_TOKEN);
  }
}
