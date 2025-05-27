package com.heez.urlib.domain.bookmark.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class BookmarkNotFoundException extends AbstractGlobalException {

  public BookmarkNotFoundException() {
    super(ErrorCode.NOT_FOUND_ENTITY);
  }
}
