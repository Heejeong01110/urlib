package com.heez.urlib.domain.auth.config;

import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.jwt.JwtHeaderUtil;
import com.heez.urlib.domain.auth.model.CustomOAuth2User;
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
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthTokenProvider authTokenProvider;
  private final RedisService redis;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    Long memberId = oAuth2User.getMemberId();
    String email = oAuth2User.getEmail();
    List<SimpleGrantedAuthority> authorities = toSimpleAuthorities(oAuth2User.getAuthorities());

    String accessToken = authTokenProvider.generateAccessToken(memberId, email, authorities);
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
