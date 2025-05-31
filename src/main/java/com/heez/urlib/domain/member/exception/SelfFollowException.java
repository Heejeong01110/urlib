package com.heez.urlib.domain.member.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class SelfFollowException extends AbstractGlobalException {

  public SelfFollowException() {
    super(ErrorCode.SELF_FOLLOW);
  }
}
