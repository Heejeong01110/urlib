package com.heez.urlib.domain.link.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class LinkNotFoundException extends AbstractGlobalException {

  public LinkNotFoundException() {
    super(ErrorCode.LINK_NOT_FOUND);
  }
}
