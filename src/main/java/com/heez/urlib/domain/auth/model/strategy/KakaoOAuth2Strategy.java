package com.heez.urlib.domain.auth.model.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class KakaoOAuth2Strategy implements OAuth2Strategy {

  @Override
  public AuthType getOAuth2ProviderType() {

    return AuthType.KAKAO;
  }

  @Override
  public OAuth2UserInfo getUserInfo(OAuth2User user) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.convertValue(user.getAttributes(), JsonNode.class);

    String oAuthId = root
        .path("id")
        .asText();

    String oAuthNickname = root
        .path("properties")
        .path("nickname")
        .asText();

    String oAuthEmail = root
        .path("kakao_account")
        .path("email")
        .asText();

    String oAuthImageUrl = root
        .path("properties")
        .path("profile_image")
        .asText();

    return new OAuth2UserInfo(AuthType.KAKAO, oAuthId, oAuthNickname, oAuthEmail, oAuthImageUrl);
  }
}
