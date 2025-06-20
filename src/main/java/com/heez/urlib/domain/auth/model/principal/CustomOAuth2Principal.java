package com.heez.urlib.domain.auth.model.principal;

import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.member.model.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2Principal implements OAuth2User, UserPrincipal {

  private final Long memberId;
  private final String email;
  private final Collection<? extends GrantedAuthority> authorities;
  private final Map<String, Object> attributes;
  private final String nameAttributeKey;
  private final AuthType authType;

  public CustomOAuth2Principal(
      Long memberId,
      String email,
      Collection<? extends GrantedAuthority> authorities,
      Map<String, Object> attributes,
      String nameAttributeKey,
      AuthType authType) {
    this.memberId = memberId;
    this.email = email;
    this.authorities = authorities;
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.authType = authType;
  }

  public static CustomOAuth2Principal from(Member member, Map<String, Object> attributes,
      AuthType authType) {
    return new CustomOAuth2Principal(member.getMemberId(), member.getEmail(),
        List.of(new SimpleGrantedAuthority(member.getRole().getKey())),
        attributes, "id", authType);
  }

  @Override
  public <A> A getAttribute(String name) {
    return OAuth2User.super.getAttribute(name);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return String.valueOf(attributes.get(nameAttributeKey));
  }

  @Override
  public Long getMemberId() {
    return memberId;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public AuthType getAuthType() {
    return authType;
  }
}
