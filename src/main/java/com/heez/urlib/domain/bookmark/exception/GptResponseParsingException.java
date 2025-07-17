package com.heez.urlib.domain.bookmark.exception;

import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.handler.ErrorCode;

public class GptResponseParsingException extends AbstractGlobalException {

  public GptResponseParsingException() {
    super(ErrorCode.GPT_RESPONSE_PARSING_EXCEPTION);
  }
}
