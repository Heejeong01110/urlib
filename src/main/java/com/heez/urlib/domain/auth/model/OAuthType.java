package com.heez.urlib.domain.auth.model;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthType {
  KAKAO("KAKAO");

  private final String provider;

  public static OAuthType ofType(String provider) {
    return Arrays.stream(values())
        .filter(OAuthType -> OAuthType.provider.equals(provider.toUpperCase()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No such OauthType"));
  }

}
