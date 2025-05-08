package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.response.ErrorCode;

public class ExpiredRefreshTokenException extends AbstractGlobalException {

  public ExpiredRefreshTokenException() {
    super(ErrorCode.EXPIRED_REFRESH_TOKEN);
  }
}
