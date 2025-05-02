package com.heez.urlib.domain.auth.model;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

  private final Long memberId;
  private final String email;

  public CustomOAuth2User(
      Long memberId,
      String email,
      Collection<? extends GrantedAuthority> authorities,
      Map<String, Object> attributes, String nameAttributeKey) {
    super(authorities, attributes, nameAttributeKey);
    this.memberId = memberId;
    this.email = email;
  }
}
