package com.heez.urlib.domain.auth.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import com.heez.urlib.domain.auth.model.OAuthType;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class KakaoOAuth2Strategy implements OAuth2Strategy {

  @Override
  public OAuthType getOAuth2ProviderType() {

    return OAuthType.KAKAO;
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

    return new OAuth2UserInfo(OAuthType.KAKAO, oAuthId, oAuthNickname, oAuthEmail, oAuthImageUrl);
  }
}
