package com.heez.urlib.domain.auth.model;

public record OAuth2UserInfo(OAuthType oAuthType,
                             String oAuthId,
                             String nickname,
                             String email,
                             String imageUrl) {

}
