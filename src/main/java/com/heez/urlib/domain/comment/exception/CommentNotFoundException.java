package com.heez.urlib.domain.comment.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class CommentNotFoundException extends AbstractGlobalException {

  public CommentNotFoundException() {
    super(ErrorCode.COMMENT_NOT_FOUND);
  }
}
