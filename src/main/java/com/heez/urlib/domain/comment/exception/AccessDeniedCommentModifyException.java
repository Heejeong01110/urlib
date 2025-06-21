package com.heez.urlib.domain.comment.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class AccessDeniedCommentModifyException extends AbstractGlobalException {

  public AccessDeniedCommentModifyException() {
    super(ErrorCode.ACCESS_DENIED_COMMENT_MODIFY);
  }
}
