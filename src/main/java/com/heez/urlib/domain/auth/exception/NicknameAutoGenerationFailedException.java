package com.heez.urlib.domain.auth.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class NicknameAutoGenerationFailedException  extends AbstractGlobalException {

  public NicknameAutoGenerationFailedException() {
    super(ErrorCode.NICKNAME_AUTO_GENERATION_FAILED);
  }
}
