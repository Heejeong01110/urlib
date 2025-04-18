package com.heez.urlib.domain.auth.config;

import com.heez.urlib.domain.auth.exception.SecurityExceptionHandlerFilter;
import com.heez.urlib.domain.auth.jwt.JwtAuthenticationFilter;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final SecurityExceptionHandlerFilter securityExceptionHandlerFilter;
  private final JwtAuthenticationFilter jwtTokenValidationFilter;


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


    return http
        .authorizeHttpRequests(request -> request
            .requestMatchers(
                Stream
                    .of(PermitAllEndpoint.permitAllArray)
                    .map(AntPathRequestMatcher::antMatcher)
                    .toArray(AntPathRequestMatcher[]::new)
            )
            .permitAll()
            .anyRequest().authenticated())
        .cors(cors -> corsFilter())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(HttpBasicConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .addFilterBefore(jwtTokenValidationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(securityExceptionHandlerFilter, JwtAuthenticationFilter.class)
        .build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().requestMatchers(
        "assets/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/api-docs/**",
        "/h2-console/**"
    );
  }


  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);  //내 서버가 응답을 할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
    config.addAllowedOriginPattern("*");  // 모든 ip에 응답을 허용
    config.addAllowedHeader("*");  // 모든 header에 응답을 허용
    config.addAllowedMethod("*");  // 모든 post, get, put, delete, patch 요청을 허용
    source.registerCorsConfiguration("/api/v1/**", config);
    return new CorsFilter(source);
  }


}
