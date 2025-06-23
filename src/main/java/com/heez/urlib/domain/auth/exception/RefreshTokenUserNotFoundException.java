package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class RefreshTokenUserNotFoundException extends AbstractGlobalException {

  public RefreshTokenUserNotFoundException() {
    super(ErrorCode.REFRESH_TOKEN_USER_NOT_FOUND);
  }
}
