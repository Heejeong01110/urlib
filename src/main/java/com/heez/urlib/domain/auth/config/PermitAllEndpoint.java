package com.heez.urlib.domain.auth.config;

//인증이 필요하지 않은 API 목록을 정의하는 클래스입니다.
public class PermitAllEndpoint {

  private PermitAllEndpoint() {
  }

  protected static final String[] permitAllArray = new String[]{
      "/",
      "/auth/kakao",
      "/auth/google",
      "/error",
      "/docs/**",
      "/favicon.ico",
      "/swagger-ui/**",
      "/swagger-resources/**",
      "/v1/api-docs/**",
      "/actuator/**",
      "/metrics/**",
      "/assets/**",
      "/api-docs/**",
      "/h2-console/**"
  };
}
