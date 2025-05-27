package com.heez.urlib.domain.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.global.error.handler.ErrorCode;
import com.heez.urlib.global.error.handler.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

// 인가 실패(권한 없음) 처리
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    ErrorCode errorCode = ErrorCode.FORBIDDEN;
    ErrorResponse body = ErrorResponse.of(errorCode, accessDeniedException.getMessage());
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }

}
