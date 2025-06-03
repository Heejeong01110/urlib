package com.heez.urlib.domain.auth.model;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthType {
  KAKAO("KAKAO"),
  EMAIL("EMAIL"),
  NONE("NONE");

  private final String provider;

  public static AuthType ofType(String provider) {
    return Arrays.stream(values())
        .filter(OAuthType -> OAuthType.provider.equals(provider.toUpperCase()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No such OauthType"));
  }

}
