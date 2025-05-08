package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.response.ErrorCode;

public class MissingJwtTokenException extends AbstractJwtException {

  public MissingJwtTokenException() {
    super(ErrorCode.MISSING_TOKEN);
  }
}
