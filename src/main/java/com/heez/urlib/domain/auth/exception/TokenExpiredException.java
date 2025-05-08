package com.heez.urlib.domain.auth.exception;


import com.heez.urlib.global.error.response.ErrorCode;

public class TokenExpiredException extends AbstractJwtException {

  public TokenExpiredException() {
    super(ErrorCode.EXPIRED_TOKEN);
  }

}
