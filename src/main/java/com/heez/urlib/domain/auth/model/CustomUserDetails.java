package com.heez.urlib.domain.auth.model;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {
  private final String name;
  private final String email;
  private final List<? extends GrantedAuthority> authorities;
  private Map<String, Object> attributes;

  @Serial
  private static final long serialVersionUID = 0L;

  //일반 로그인
  @Builder
  public CustomUserDetails(String name, String email, List<? extends GrantedAuthority> authorities) {
    this.name = name;
    this.email = email;
    this.authorities = authorities;
  }

  //OAuth 로그인
  @Builder
  public CustomUserDetails(String name, String email, List<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
    this.name = name;
    this.email = email;
    this.authorities = authorities;
    this.attributes = attributes;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (CustomUserDetails) obj;
    return Objects.equals(this.name, that.name)
        && Objects.equals(this.email, that.email)
        && Objects.equals(this.authorities, that.authorities)
        && Objects.equals(this.attributes, that.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), getEmail(), getAuthorities(), getAttributes());
  }

  @Override
  public String toString() {
    return "CustomUser[" +
        "name=" + name + ", " +
        "email=" + email + ", " +
        "authorities=" + authorities + ", " +
        "attributes=" + attributes + ']';
  }

  @Override
  public <A> A getAttribute(String name) {
    return OAuth2User.super.getAttribute(name);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return "";
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
