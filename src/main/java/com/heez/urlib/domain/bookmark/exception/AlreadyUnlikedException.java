package com.heez.urlib.domain.bookmark.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class AlreadyUnlikedException extends AbstractGlobalException {

  public AlreadyUnlikedException() {
    super(ErrorCode.ALREADY_UNLIKED_BOOKMARK);
  }
}
