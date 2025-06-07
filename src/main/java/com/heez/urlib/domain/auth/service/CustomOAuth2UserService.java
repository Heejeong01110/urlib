package com.heez.urlib.domain.auth.service;

import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import com.heez.urlib.domain.auth.model.principal.CustomOAuth2Principal;
import com.heez.urlib.domain.auth.model.strategy.OAuth2StrategyComposite;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final MemberService memberService;
  private final OAuth2StrategyComposite oAuth2StrategyComposite;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);
    OAuth2UserInfo userInfo = oAuth2StrategyComposite
        .getOAuth2Strategy(getSocialProvider(userRequest))
        .getUserInfo(oAuth2User);

    Member member = memberService.findMemberOrCreate(userInfo);
    return CustomOAuth2Principal.from(member, oAuth2User.getAttributes(), userInfo.authType());
  }

  private AuthType getSocialProvider(OAuth2UserRequest userRequest) {
    return AuthType.ofType(userRequest.getClientRegistration().getRegistrationId());
  }
}
