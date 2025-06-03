package com.heez.urlib.domain.auth.model;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface UserPrincipal {

  Long getMemberId();

  String getEmail();

  Collection<? extends GrantedAuthority> getAuthorities();

  AuthType getAuthType();
}
