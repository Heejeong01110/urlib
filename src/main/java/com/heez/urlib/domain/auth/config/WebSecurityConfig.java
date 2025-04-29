package com.heez.urlib.domain.auth.config;

import com.heez.urlib.domain.auth.exception.SecurityExceptionHandlerFilter;
import com.heez.urlib.domain.auth.jwt.JwtAuthenticationFilter;
import com.heez.urlib.domain.auth.service.CustomOAuth2UserService;
import com.heez.urlib.global.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final SecurityExceptionHandlerFilter securityExceptionHandlerFilter;
  private final JwtAuthenticationFilter jwtTokenValidationFilter;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final CustomOAuth2UserService oAuth2UserService;
  private final OAuth2FailureHandler oAuth2FailureHandler;
  private final CorsConfig corsSource;

  // OAuth2 처리하는 체인
  @Bean
  @Order(1)
  public SecurityFilterChain oauth2Chain(HttpSecurity http) throws Exception {
    applyCommon(http);
    http
        .securityMatcher("/api/*/auth/**",
            "/oauth2/authorization/**",
            "/login/oauth2/code/kakao")
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())
        .oauth2Login(o -> o
            .userInfoEndpoint(u -> u.userService(oAuth2UserService))
            .successHandler(oAuth2SuccessHandler)
            .failureHandler(oAuth2FailureHandler)
        );
    return http.build();
  }

  // JWT 검증 처리하는 체인
  @Bean
  @Order(2)
  SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
    applyCommon(http);
    http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(a -> a
            .requestMatchers("/api/*/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(handler -> handler
            .authenticationEntryPoint(new CustomJwtAuthenticationEntryPoint())
            .accessDeniedHandler(new CustomAccessDeniedHandler()))
        .addFilterBefore(jwtTokenValidationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(securityExceptionHandlerFilter, ExceptionTranslationFilter.class);
    return http.build();
  }

  private void applyCommon(HttpSecurity http) throws Exception {
    http
        .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // CORS
        .cors(c -> c.configurationSource(corsSource.corsConfigurationSource()))
        // CSRF
        .csrf(AbstractHttpConfigurer::disable)
        // 기본 인증 폼 / HTTP Basic 비활성화
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        // 헤더 프레임 옵션 해제 (예: H2 Console)
        .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
  }

}
