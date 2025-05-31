package com.heez.urlib.global.error.handler;

import lombok.Getter;

@Getter
public enum ErrorCode {
  //global
  INTERNAL_SERVER_ERROR("G001", "Internal Server Error"),
  INVALID_INPUT_VALUE_ERROR("G002", "유효하지 않은 입력값입니다."),
  INVALID_METHOD_ERROR("G003", "Method Argument가 적절하지 않습니다."),
  REQUEST_BODY_MISSING_ERROR("G004", "RequestBody에 데이터가 존재하지 않습니다."),
  REQUEST_PARAM_MISSING_ERROR("G005", "RequestParam에 데이터가 전달되지 않았습니다."),
  INVALID_TYPE_VALUE_ERROR("G006", "타입이 유효하지 않습니다."),
  NOT_FOUND_ENTITY("G007", "엔티티를 찾을 수 없습니다."),
  UTIL_NOT_CONSTRUCTOR("G008", "유틸클래스는 생성자를 호출할 수 없습니다."),

  ILLEGAL_ARGUMENT_ERROR("E001", "잘못된 입력입니다."),
  ILLEGAL_STATE_ERROR("E002", "잘못된 상태입니다"),

  //login
  EXPIRED_TOKEN("L001", "토큰이 만료되었습니다."),
  EXPIRED_REFRESH_TOKEN("L002", "리프레쉬 토큰도 만료되어 다시 로그인을 요청합니다."),
  UNAUTHORIZED_TOKEN("L003", "인증되지 않은 토큰입니다."),
  OAUTH_CLIENT_SERVER_ERROR("L004", "oauth 클라이언트 서버 에러입니다."),
  IS_LOGOUT_TOKEN("L005", "이미 로그아웃한 토큰입니다."),
  FORBIDDEN("L006", "권한이 없는 사용자입니다."),

  // --- JWT 검증 관련 신규 코드 ---
  UNSUPPORTED_TOKEN("L007", "지원되지 않는 JWT 토큰입니다."),
  INVALID_TOKEN_FORMAT("L008", "잘못된 JWT 토큰 형식입니다."),
  INVALID_SIGNATURE("L009", "JWT 토큰 서명 검증에 실패했습니다."),
  MISSING_TOKEN("L010", "JWT 토큰이 존재하지 않습니다."),
  TOKEN_PROCESSING_ERROR("L011", "JWT 토큰 처리 중 알 수 없는 오류가 발생했습니다."),

  //Member
  NOT_FOUND_MEMBER("M001", "사용자 정보가 존재하지 않습니다."),
  NOT_FOUND_EMAIL("M002", "사용자 email 정보가 존재하지 않습니다."),

  //Bookmark
  ACCESS_DENIED_BOOKMARK("B001", "북마크 접근 권한이 없습니다."),
  ACCESS_DENIED_BOOKMARK_MODIFY("B002", "북마크 수정 권한이 없습니다."),
  ALREADY_LIKED_BOOKMARK("B003", "이미 좋아요 한 북마크입니다."),
  ALREADY_UNLIKED_BOOKMARK("B004", "이미 좋아요를 취소한 북마크입니다.");

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }


}
