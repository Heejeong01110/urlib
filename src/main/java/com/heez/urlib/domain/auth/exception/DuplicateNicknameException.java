package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class DuplicateNicknameException  extends AbstractGlobalException {

  public DuplicateNicknameException() {
    super(ErrorCode.DUPLICATE_NICKNAME);
  }
}
