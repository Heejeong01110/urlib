package com.heez.urlib.domain.auth.model.principal;

import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.member.model.Member;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUsernamePasswordPrincipal implements UserDetails, UserPrincipal {

  private final Long memberId;
  private final String email;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final AuthType authType;

  public CustomUsernamePasswordPrincipal(Long memberId,
      String email,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      AuthType authType) {
    this.memberId = memberId;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
    this.authType = authType;
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

  @Override
  public String getPassword() {
    return password;
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
