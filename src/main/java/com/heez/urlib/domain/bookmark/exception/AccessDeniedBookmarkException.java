package com.heez.urlib.domain.bookmark.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.response.ErrorCode;

public class AccessDeniedBookmarkException extends AbstractGlobalException {

  public AccessDeniedBookmarkException() {
    super(ErrorCode.ACCESS_DENIED_BOOKMARK);
  }
}
