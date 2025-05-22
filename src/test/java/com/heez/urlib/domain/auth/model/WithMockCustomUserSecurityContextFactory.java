package com.heez.urlib.domain.auth.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    // 권한 목록
    List<GrantedAuthority> authorities = Arrays.stream(annotation.roles())
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    // 속성 맵
    Map<String, Object> attributes = Map.of(
        "id", String.valueOf(annotation.memberId()),
        "email", annotation.email()
    );

    // principal 생성 (필요시 UserDetails 구현도 포함)
    CustomOAuth2User principal = new CustomOAuth2User(
        annotation.memberId(),
        annotation.email(),
        authorities,
        attributes,
        "id"
    );

    // OAuth2AuthenticationToken 생성
    OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(
        principal,
        authorities,
        annotation.registration()
    );

    context.setAuthentication(auth);
    return context;
  }
}
