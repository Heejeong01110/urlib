package com.heez.urlib.domain.auth.model;

import lombok.Builder;

@Builder
public record OAuth2UserInfo(AuthType authType,
                             String oAuthId,
                             String nickname,
                             String email,
                             String imageUrl) {

}
