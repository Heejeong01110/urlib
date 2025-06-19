package com.heez.urlib.domain.bookmark.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class BookmarkShareNotFoundException extends AbstractGlobalException {

  public BookmarkShareNotFoundException() {
    super(ErrorCode.BOOKMARK_SHARE_NOT_FOUND);
  }
}
