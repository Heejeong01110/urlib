package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.response.ErrorCode;

public class InvalidRefreshTokenException extends AbstractGlobalException {

  public InvalidRefreshTokenException() {
    super(ErrorCode.UNAUTHORIZED_TOKEN);
  }
}
