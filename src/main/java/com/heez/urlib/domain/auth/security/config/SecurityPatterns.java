package com.heez.urlib.domain.auth.security.config;


import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

public class SecurityPatterns {

  public static final List<MatcherRule> RULES = List.of(
      permitAll(POST, "/api/*/auth/signup"),
      permitAll(POST, "/api/*/auth/login"),
      permitAll(GET, "/api/*/bookmarks"),
      permitAll(GET, "/api/*/bookmarks/*"),
      permitAll(GET, "/api/*/users/*"),
      permitAll(GET, "/api/*/users/*/bookmarks"),
      permitAll(GET, "/api/*/users/*/follow"),
      permitAll(GET, "/api/*/users/*/following"),
      permitAll(GET, "/api/*/users/*/follower")
  );

  private static MatcherRule permitAll(HttpMethod method, String pattern) {
    return new MatcherRule(method, pattern,
        (auth, r) -> auth.requestMatchers(r.method(), r.pattern()).permitAll());
  }

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
