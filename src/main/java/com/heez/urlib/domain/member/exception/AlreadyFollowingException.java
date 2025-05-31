package com.heez.urlib.domain.member.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class AlreadyFollowingException extends AbstractGlobalException {

  public AlreadyFollowingException() {
    super(ErrorCode.ALREADY_FOLLOWING_MEMBER);
  }
}
