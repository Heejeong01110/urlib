package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.handler.ErrorCode;
import lombok.Getter;

@Getter
public class TokenValidFailedException extends AbstractJwtException {

  public TokenValidFailedException() {
    super(ErrorCode.EXPIRED_TOKEN);
  }

}
