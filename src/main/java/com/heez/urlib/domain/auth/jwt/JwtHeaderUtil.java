package com.heez.urlib.domain.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;

public class JwtHeaderUtil {
  private static final String HEADER_AUTHORIZATION = "X-AUTH-TOKEN";
  private static final String TOKEN_PREFIX = "Bearer ";


  public static String resolveToken(HttpServletRequest request) {

    String header = request.getHeader(HEADER_AUTHORIZATION);

    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
      return null;
    }

    return header.substring(TOKEN_PREFIX.length());
  }
}
