package com.heez.urlib.domain.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {


  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    log.error("error : ", exception);
    if (exception instanceof OAuth2AuthenticationException) {
      OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
      log.error("OAuth2 error code: {}", error.getErrorCode());
      log.error("OAuth2 description: {}", error.getDescription());
      log.error("OAuth2 uri: {}", error.getUri());
    }

  }
}
