package com.heez.urlib.domain.auth.controller;

import com.heez.urlib.domain.auth.controller.dto.SignUpRequest;
import com.heez.urlib.domain.auth.security.jwt.JwtHeaderUtil;
import com.heez.urlib.domain.auth.service.AuthService;
import com.heez.urlib.domain.auth.service.dto.ReissueDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<Void> signup(
      @Valid @RequestBody SignUpRequest request) {
    authService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/re-issue")
  public ResponseEntity<Void> reissue(
      HttpServletRequest request,
      HttpServletResponse response) {
    String refreshToken = JwtHeaderUtil.resolveRefreshToken(request);
    ReissueDto tokens = authService.reissue(refreshToken);

    response.setHeader(JwtHeaderUtil.HEADER_AUTHORIZATION,
        JwtHeaderUtil.TOKEN_PREFIX + tokens.accessToken());
    response.addHeader(HttpHeaders.SET_COOKIE, JwtHeaderUtil.toCookie(tokens.refreshToken()));
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    authService.logout(JwtHeaderUtil.resolveRefreshToken(request));

    response.setHeader(JwtHeaderUtil.HEADER_AUTHORIZATION, "");
    response.setHeader(HttpHeaders.SET_COOKIE, JwtHeaderUtil.deleteCookie());
    return ResponseEntity.noContent().build();
  }
}
