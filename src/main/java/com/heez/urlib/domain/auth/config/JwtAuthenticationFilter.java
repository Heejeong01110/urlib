package com.heez.urlib.domain.auth.config;

import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.jwt.JwtHeaderUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final AuthTokenProvider tokenProvider;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getServletPath();
    // JWT 필터를 제외할 URI
    return Stream.of(
        "/api/*/auth/re-issue"
    ).anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    JwtHeaderUtil.resolveAccessToken(request).ifPresent(token -> {
      tokenProvider.validateAccessToken(token);
      Authentication auth = tokenProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
    });
    filterChain.doFilter(request, response);
  }


}
