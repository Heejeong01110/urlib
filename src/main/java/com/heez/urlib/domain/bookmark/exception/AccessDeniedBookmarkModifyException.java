package com.heez.urlib.domain.bookmark.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class AccessDeniedBookmarkModifyException extends AbstractGlobalException {

  public AccessDeniedBookmarkModifyException() {
    super(ErrorCode.ACCESS_DENIED_BOOKMARK_MODIFY);
  }
}
