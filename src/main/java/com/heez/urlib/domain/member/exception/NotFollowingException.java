package com.heez.urlib.domain.member.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class NotFollowingException extends AbstractGlobalException {

  public NotFollowingException() {
    super(ErrorCode.NOT_FOLLOWING_MEMBER);
  }
}
