package com.heez.urlib.domain.auth.controller;

import com.heez.urlib.domain.auth.controller.dto.SignUpRequest;
import com.heez.urlib.domain.auth.security.jwt.JwtHeaderUtil;
import com.heez.urlib.domain.auth.service.AuthService;
import com.heez.urlib.domain.auth.service.dto.ReissueDto;
import com.heez.urlib.global.swagger.ApiErrorResponses_BadRequest_Forbidden_Conflict;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "인증 API", description = "인증 관련 기능 제공")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "회원가입",
      description = "이메일, 비밀번호, 닉네임을 통해 회원가입을 진행합니다."
  )
  @ApiResponse(responseCode = "201", description = "회원가입 성공")
  @ApiErrorResponses_BadRequest_Forbidden_Conflict
  @PostMapping("/signup")
  public ResponseEntity<Void> signup(
      @Valid @RequestBody SignUpRequest request) {
    authService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(
      summary = "Token 재발급",
      description = """
          저장된 Refresh Token을 기반으로 새로운 Access Token을 발급합니다.
           - 요청 시 반드시 `Refresh Token`이 쿠키 또는 헤더에 포함되어 있어야 합니다.
           - 응답 헤더에는 새로운 Access Token이 Authorization 헤더로 반환되고,
           - 새로운 Refresh Token은 Set-Cookie 헤더로 내려갑니다.
          """
  )
  @ApiResponse(responseCode = "204", description = "재발급 성공")
  @ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않음 또는 존재하지 않음")
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
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "로그아웃",
      description = """
          서버에서 Refresh Token을 무효화하여 로그아웃을 수행합니다.
          - 요청 시 Refresh Token은 쿠키 또는 헤더로 전달되어야 합니다.
          - 응답 시 Access Token은 빈 Authorization 헤더로 초기화되고,
          - Refresh Token은 삭제된 쿠키로 반환됩니다.
          """
  )
  @ApiResponse(responseCode = "204", description = "로그아웃 성공")
  @ApiResponse(responseCode = "401", description = "Refresh Token이 없거나 유효하지 않음")
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
