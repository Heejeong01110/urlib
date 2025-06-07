package com.heez.urlib.domain.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.controller.dto.LoginRequest;
import com.heez.urlib.domain.auth.security.handler.CustomFailureHandler;
import com.heez.urlib.domain.auth.security.handler.CustomSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

public class CustomUsernamePasswordAuthenticationFilter extends
    AbstractAuthenticationProcessingFilter {

  private static final String CONTENT_TYPE = "application/json";
  private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
      new AntPathRequestMatcher("/api/*/auth/login", "POST");

  private final ObjectMapper objectMapper;

  public CustomUsernamePasswordAuthenticationFilter(
      ObjectMapper objectMapper,
      CustomSuccessHandler successHandler,
      CustomFailureHandler failureHandler,
      AuthenticationManager authManager) {
    super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
    this.objectMapper = objectMapper;
    setAuthenticationManager(authManager);
    setAuthenticationSuccessHandler(successHandler);
    setAuthenticationFailureHandler(failureHandler);
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws AuthenticationException, IOException {
    if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
      throw new AuthenticationServiceException(
          "Authentication Content-Type not supported: " + request.getContentType());
    }

    String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
    LoginRequest loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);

    return this.getAuthenticationManager().authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
  }
}
