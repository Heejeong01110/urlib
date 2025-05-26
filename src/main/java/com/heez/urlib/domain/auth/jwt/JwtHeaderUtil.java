package com.heez.urlib.domain.auth.jwt;

import com.heez.urlib.domain.auth.exception.MissingJwtTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.util.WebUtils;

@Slf4j
public class JwtHeaderUtil {

  public static final String HEADER_AUTHORIZATION = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_REFRESH_COOKIE = "__Host-refresh_token";

  public static Optional<String> resolveAccessToken(HttpServletRequest request) {
    String header = request.getHeader(HEADER_AUTHORIZATION);
    if (header != null && header.startsWith(TOKEN_PREFIX)) {
      return Optional.of(header.substring(TOKEN_PREFIX.length()).trim());
    }
    return Optional.empty();
  }

  public static String resolveRefreshToken(HttpServletRequest request) {
    Cookie refreshCookie = Optional.ofNullable(
            WebUtils.getCookie(request, HEADER_REFRESH_COOKIE))
        .orElseThrow(MissingJwtTokenException::new);
    return URLDecoder.decode(refreshCookie.getValue(), StandardCharsets.UTF_8);
  }


  public static String toCookie(String refreshToken) {
    return ResponseCookie.from(HEADER_REFRESH_COOKIE, refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite("Strict")
        .build()
        .toString();
  }

  public static String deleteCookie() {
    return ResponseCookie.from(HEADER_REFRESH_COOKIE, "")
        .path("/")
        .httpOnly(true)
        .maxAge(0)
        .build()
        .toString();
  }
}
