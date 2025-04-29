package com.heez.urlib.domain.auth.config;

import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.jwt.JwtHeaderUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthTokenProvider authTokenProvider;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    String email = oAuth2User.getAttribute("email");
    String nickname = oAuth2User.getAttribute("nickname");
    List<SimpleGrantedAuthority> authorities = null;
    if(oAuth2User.getAuthorities().isEmpty()){
      authorities = (List<SimpleGrantedAuthority>) oAuth2User.getAuthorities();
    }
    String accessToken = authTokenProvider.generateAccessToken(nickname, email, authorities);
    String refreshToken = authTokenProvider.generateRefreshToken(email);

    response.setHeader(JwtHeaderUtil.HEADER_AUTHORIZATION,
        JwtHeaderUtil.TOKEN_PREFIX + accessToken);
    //리프레시 토큰 저장
    Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(7*24*60*60);
    response.addCookie(refreshCookie);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");
  }
}
