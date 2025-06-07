package com.heez.urlib.domain.auth.model.strategy;

import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Strategy {
  AuthType getOAuth2ProviderType();

  OAuth2UserInfo getUserInfo(OAuth2User user);

  default void isOauthIdExist(String oauthId) {
    if (null == oauthId) {
      throw new OAuth2AuthenticationException("oauthId does not exist");
    }
  }
}
