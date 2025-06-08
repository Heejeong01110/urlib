package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class DuplicateEmailByEmailTypeException extends AbstractGlobalException {

  public DuplicateEmailByEmailTypeException() {
    super(ErrorCode.DUPLICATE_EMAIL_BY_EMAIL);
  }
}
