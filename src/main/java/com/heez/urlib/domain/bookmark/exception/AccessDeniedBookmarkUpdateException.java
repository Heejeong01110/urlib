package com.heez.urlib.domain.bookmark.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.response.ErrorCode;

public class AccessDeniedBookmarkUpdateException extends AbstractGlobalException {

  public AccessDeniedBookmarkUpdateException() {
    super(ErrorCode.ACCESS_DENIED_BOOKMARK_UPDATE);
  }
}
