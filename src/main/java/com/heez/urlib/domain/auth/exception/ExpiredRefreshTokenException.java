package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractJwtException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class ExpiredRefreshTokenException extends AbstractJwtException {

  public ExpiredRefreshTokenException() {
    super(ErrorCode.EXPIRED_REFRESH_TOKEN);
  }
}
