package com.heez.urlib.domain.auth.model;

import com.heez.urlib.domain.auth.model.principal.CustomUsernamePasswordPrincipal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    List<GrantedAuthority> authorities = Arrays.stream(annotation.roles())
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    CustomUsernamePasswordPrincipal principal = new CustomUsernamePasswordPrincipal(
        annotation.memberId(),
        annotation.email(),
        null,
        authorities,
        annotation.authType()
    );

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(principal, null, authorities);
    context.setAuthentication(auth);
    return context;
  }
}
