package com.heez.urlib.global.common.openai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

  private final String model;
  private final Api api;


  @Getter
  @RequiredArgsConstructor
  public static class Api {

    private final String url;
    private final String key;
  }
}
