package com.heez.urlib.domain.bookmark.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class AlreadyLikedException extends AbstractGlobalException {

  public AlreadyLikedException() {
    super(ErrorCode.ALREADY_LIKED_BOOKMARK);
  }
}
