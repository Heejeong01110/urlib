package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractJwtException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class InvalidJwtSignatureException extends AbstractJwtException {

  public InvalidJwtSignatureException() {
    super(ErrorCode.INVALID_SIGNATURE);
  }
}
