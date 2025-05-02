package com.heez.urlib.domain.auth.config;

import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.jwt.JwtHeaderUtil;
import com.heez.urlib.domain.auth.model.RefreshToken;
import com.heez.urlib.domain.auth.repository.RefreshTokenRepository;
import com.heez.urlib.domain.auth.repository.entity.TokenEntity;
import com.heez.urlib.domain.member.repository.MemberRepository;
import com.heez.urlib.global.exception.ErrorMessage;
import com.heez.urlib.global.exception.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final MemberRepository memberRepository;

  private final AuthTokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (isRequestPassURI(request, response, filterChain)) {
      return;
    }

    String accessToken = JwtHeaderUtil.resolveToken(request);
    if (accessToken != null && tokenProvider.validateAccessToken(accessToken)) {
      Authentication auth = tokenProvider.getAuthentication(accessToken);
      SecurityContextHolder.getContext().setAuthentication(auth);
    } else {
      Cookie refreshCookie = WebUtils.getCookie(request, "refreshToken");
      if (refreshCookie != null) {
        String refreshToken = URLDecoder.decode(refreshCookie.getValue(), StandardCharsets.UTF_8);

        // 3) JWT 서명·만료 검증
        if (tokenProvider.validateRefreshToken(refreshToken)) {

          // 4) Redis 또는 DB에서 실제로 저장된 토큰과 일치하는지 확인
          Optional<RefreshToken> redisRefreshToken = refreshTokenRepository.findById(refreshToken);

          if (redisRefreshToken.isPresent()) {
            // 5) 유효한 경우 새로운 Access Token 발급 & SecurityContext 세팅
            Long memberId = redisRefreshToken.get().getId();
            TokenEntity tokenEntity = memberRepository.findEmailAndRoleById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, memberId));

            String newAccessToken = tokenProvider.generateAccessToken(memberId,
                tokenEntity.email().getValue(),
                List.of(new SimpleGrantedAuthority(tokenEntity.role().name())));

            // (원하면) 응답 헤더에 새 Access Token 추가
            response.setHeader("Authorization", "Bearer " + newAccessToken);

            Authentication auth = tokenProvider.getAuthentication(newAccessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
          } else {
            // DB/Redis에 토큰이 없으면 로그아웃 처리
            clearRefreshCookie(response);
          }
        } else {
          // 서명이 틀리거나 만료된 경우 → 쿠키 만료
          clearRefreshCookie(response);
          //로그아웃
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  private void clearRefreshCookie(HttpServletResponse response) {
    ResponseCookie expired = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build();
    response.setHeader(HttpHeaders.SET_COOKIE, expired.toString());
  }

  private boolean isRequestPassURI(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) {
    return false;
  }


}
