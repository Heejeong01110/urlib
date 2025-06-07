package com.heez.urlib.domain.auth.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.security.filter.CustomUsernamePasswordAuthenticationFilter;
import com.heez.urlib.domain.auth.security.filter.JwtAuthenticationFilter;
import com.heez.urlib.domain.auth.security.handler.CustomAccessDeniedHandler;
import com.heez.urlib.domain.auth.security.handler.CustomFailureHandler;
import com.heez.urlib.domain.auth.security.handler.CustomJwtAuthenticationEntryPoint;
import com.heez.urlib.domain.auth.security.handler.CustomSuccessHandler;
import com.heez.urlib.domain.auth.service.CustomOAuth2UserService;
import com.heez.urlib.domain.auth.service.CustomUserDetailsService;
import com.heez.urlib.global.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final String[] allowedUrls = {"/api/*/auth/login", "/api/*/auth/re-issue",
      "/api/v1/auth/signup"};
  private final CustomOAuth2UserService oAuth2UserService;
  private final CustomSuccessHandler customSuccessHandler;
  private final CustomFailureHandler customFailureHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomUserDetailsService customUserDetailsService;
  private final CustomJwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final CorsConfig corsConfig;
  private final ObjectMapper objectMapper;

  // OAuth2 처리하는 체인
  @Bean
  @Order(1)
  public SecurityFilterChain oauth2Chain(HttpSecurity http) throws Exception {
    applyCommon(http);
    http
        .securityMatcher("/oauth2/authorization/**",
            "/login/oauth2/code/kakao")
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())
        .oauth2Login(o -> o
            .userInfoEndpoint(u -> u.userService(oAuth2UserService))
            .successHandler(customSuccessHandler)
            .failureHandler(customFailureHandler)
        );
    return http.build();
  }

  // JWT 검증 처리하는 체인
  @Bean
  @Order(2)
  SecurityFilterChain apiChain(HttpSecurity http, AuthenticationManager authenticationManager)
      throws Exception {
    applyCommon(http);
    http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(a -> a
            .requestMatchers("/api/*/auth/**").permitAll()
        )
        .authorizeHttpRequests(this::applySecurityPatterns)
        .authenticationProvider(daoAuthenticationProvider())
        .exceptionHandling(handler -> handler
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .addFilterBefore(
            usernamePasswordAuthenticationFilter(authenticationManager),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(
            jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  private void applyCommon(HttpSecurity http) throws Exception {
    http
        .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // CORS
        .cors(c -> c.configurationSource(corsConfig.corsConfigurationSource()))
        // CSRF
        .csrf(AbstractHttpConfigurer::disable)
        // 기본 인증 폼 / HTTP Basic 비활성화
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        // 헤더 프레임 옵션 해제 (예: H2 Console)
        .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
  }

  private void applySecurityPatterns(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>
          .AuthorizationManagerRequestMatcherRegistry auth
  ) {
    SecurityPatterns.RULES.forEach(rule -> rule.apply(auth));
    auth.anyRequest().permitAll();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customUserDetailsService);
    provider.setPasswordEncoder(bCryptPasswordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public CustomUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(
      AuthenticationManager authenticationManager
  ) {
    return new CustomUsernamePasswordAuthenticationFilter(
        objectMapper,
        customSuccessHandler,
        customFailureHandler,
        authenticationManager);
  }

}
