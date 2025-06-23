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
      anonymous(POST, "/api/*/auth/signup"),
      anonymous(POST, "/api/*/auth/login"),
      permitAll(GET, "/api/*/bookmarks"),
      permitAll(GET, "/api/*/bookmarks/*"),
      permitAll(GET, "/api/*/users/*"),
      permitAll(GET, "/api/*/users/*/bookmarks"),
      permitAll(GET, "/api/*/users/*/follow"),
      permitAll(GET, "/api/*/users/*/following"),
      permitAll(GET, "/api/*/users/*/follower"),
      permitAll(GET, "/api/*/users/*"),
      permitAll(GET, "/api/*/users/*/bookmarks"),
      permitAll(GET, "/api/*/bookmarks/*/comments"),
      permitAll(GET, "/api/*/comments/*/replies")
  );

  private static MatcherRule permitAll(HttpMethod method, String pattern) {
    return new MatcherRule(method, pattern,
        (auth, r) -> auth.requestMatchers(r.method(), r.pattern()).permitAll());
  }

  private static MatcherRule anonymous(HttpMethod method, String pattern) {
    return new MatcherRule(method, pattern,
        (auth, r) -> auth.requestMatchers(r.method(), r.pattern()).anonymous());
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
