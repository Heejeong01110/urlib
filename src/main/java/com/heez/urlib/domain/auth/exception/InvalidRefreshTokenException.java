package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractJwtException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class InvalidRefreshTokenException extends AbstractJwtException {

  public InvalidRefreshTokenException() {
    super(ErrorCode.UNAUTHORIZED_TOKEN);
  }
}
