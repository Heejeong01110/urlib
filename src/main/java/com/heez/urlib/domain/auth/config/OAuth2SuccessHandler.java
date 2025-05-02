package com.heez.urlib.domain.auth.config;

import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.jwt.JwtHeaderUtil;
import com.heez.urlib.domain.auth.model.CustomOAuth2User;
import com.heez.urlib.domain.auth.service.RefreshTokenService;
import com.heez.urlib.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthTokenProvider authTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final MemberRepository memberRepository;


  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    Long memberId = oAuth2User.getMemberId();
    String email = oAuth2User.getEmail();
    String accessToken;

    if (oAuth2User.getAuthorities().isEmpty()) {
      accessToken = authTokenProvider.generateAccessToken(memberId, email, new ArrayList<>());
    } else {
      accessToken = authTokenProvider.generateAccessToken(memberId, email,
          oAuth2User.getAuthorities().stream()
              .map(auth -> new SimpleGrantedAuthority(auth.getAuthority())).toList());
    }
    response.setHeader(JwtHeaderUtil.HEADER_AUTHORIZATION,
        JwtHeaderUtil.TOKEN_PREFIX + accessToken);
    //리프레시 토큰 저장
    String refreshToken = authTokenProvider.generateRefreshToken(memberId);
    refreshTokenService.saveToken(memberId, refreshToken);

    // response로 넘기기
    Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(7 * 24 * 60 * 60);
    response.addCookie(refreshCookie);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");
  }
}
