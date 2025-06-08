package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class DuplicateEmailByKakaoTypeException extends AbstractGlobalException {

  public DuplicateEmailByKakaoTypeException() {
    super(ErrorCode.DUPLICATE_EMAIL_BY_KAKAO);
  }
}
