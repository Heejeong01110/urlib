package com.heez.urlib.domain.auth.security.handler;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.security.jwt.JwtHeaderUtil;
import com.heez.urlib.domain.auth.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthTokenProvider authTokenProvider;
  private final RedisService redis;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Long memberId = userPrincipal.getMemberId();
    String email = userPrincipal.getEmail();
    List<SimpleGrantedAuthority> authorities = toSimpleAuthorities(userPrincipal.getAuthorities());

    String accessToken = authTokenProvider.generateAccessToken(memberId, email, authorities,
        userPrincipal.getAuthType());
    String refreshToken = authTokenProvider.generateRefreshToken(memberId);
    redis.saveToken(refreshToken, memberId);

    response.setHeader(JwtHeaderUtil.HEADER_AUTHORIZATION,
        JwtHeaderUtil.TOKEN_PREFIX + accessToken);
    response.addHeader(HttpHeaders.SET_COOKIE, JwtHeaderUtil.toCookie(refreshToken));
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");
  }

  private List<SimpleGrantedAuthority> toSimpleAuthorities(
      Collection<? extends GrantedAuthority> auths) {
    return auths.stream()
        .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
        .toList();
  }
}
