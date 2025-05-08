package com.heez.urlib.domain.member.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.response.ErrorCode;

public class MemberNotFoundException extends AbstractGlobalException {

  public MemberNotFoundException() {
    super(ErrorCode.NOT_FOUND_MEMBER);
  }
}
