package com.heez.urlib.global.exception;


public enum ErrorMessage {

  USER_NOT_FOUND("{0}에 해당하는 사용자 정보가 없습니다."),
  OAUTH_USER_NOT_FOUND("{0}에 해당하는 소셜 사용자 정보가 없습니다."),
  EMAIL_DUPLICATED("{0}은 중복되는 이메일 입니다."),
  NICKNAME_DUPLICATED("{0}은 중복되는 닉네임 입니다."),
  PASSWORD_VALID_FAIL("비밀번호가 잘못되었습니다."),
  UNAUTHORIZED_INVALID_TOKEN("유효하지 않은 토큰입니다.");

  private final String message;

  ErrorMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

}
