package com.heez.urlib.domain.auth.security.config;


import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.authorization.AuthenticatedAuthorizationManager.authenticated;

import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

public class SecurityPatterns {

  public static final List<MatcherRule> RULES = List.of(
      new MatcherRule(GET, "/api/*/auth/**",
          (auth, r) -> auth.requestMatchers(r.method(), r.pattern()).permitAll()),
      new MatcherRule(POST, "/api/*/auth/**",
          (auth, r) -> auth.requestMatchers(r.method(), r.pattern()).access(authenticated()))
  );

  public record MatcherRule(
      HttpMethod method,
      String pattern,
      BiConsumer<
          AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry,
          MatcherRule
          > binder
  ) {

    public void apply(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
      binder.accept(auth, this);
    }
  }
}
