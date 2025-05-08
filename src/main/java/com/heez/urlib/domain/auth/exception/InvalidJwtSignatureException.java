package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.response.ErrorCode;

public class InvalidJwtSignatureException extends AbstractJwtException {

  public InvalidJwtSignatureException() {
    super(ErrorCode.INVALID_SIGNATURE);
  }
}
