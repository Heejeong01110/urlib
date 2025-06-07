package com.heez.urlib.domain.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.exception.JwtExceptionType;
import com.heez.urlib.global.error.handler.ErrorCode;
import com.heez.urlib.global.error.handler.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// 인증 실패(토큰이 없거나 만료, 위조된 경우) 처리
@Component
public class CustomJwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    ErrorCode errorCode = (authException instanceof JwtExceptionType jwtEx)
        ? jwtEx.getErrorCode()
        : ErrorCode.UNAUTHORIZED_TOKEN;

    ErrorResponse errorResponse = ErrorResponse.of(errorCode, authException.getMessage());
    ErrorResponse body = ErrorResponse.of(errorCode, authException.getMessage());

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), errorResponse);
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }

}
