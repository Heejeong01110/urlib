package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class InvalidRefreshTokenException extends AbstractGlobalException {

  public InvalidRefreshTokenException() {
    super(ErrorCode.UNAUTHORIZED_TOKEN);
  }
}
