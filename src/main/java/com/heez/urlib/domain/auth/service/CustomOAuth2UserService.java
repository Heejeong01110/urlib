package com.heez.urlib.domain.auth.service;

import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import com.heez.urlib.domain.auth.model.OAuthType;
import com.heez.urlib.domain.auth.strategy.OAuth2StrategyComposite;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.model.vo.Nickname;
import com.heez.urlib.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final MemberRepository memberRepository;
  private final OAuth2StrategyComposite oAuth2StrategyComposite;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);
    log.info("registrationId: {}", userRequest.getClientRegistration().getRegistrationId());

    OAuth2UserInfo userInfo = oAuth2StrategyComposite
        .getOAuth2Strategy(getSocialProvider(userRequest))
        .getUserInfo(oAuth2User);

    Optional<Member> member = memberRepository.findMemberByOauthTypeAndIdentifier(
        userInfo.oAuthType(), userInfo.oAuthId());
    if (member.isEmpty()) {
      //회원가입
      memberRepository.save(Member.builder()
          .oauthType(userInfo.oAuthType())
          .identifier(userInfo.oAuthId())
          .email(new Email(userInfo.email()))
          .nickname(new Nickname(userInfo.nickname()))
          .imageUrl(userInfo.imageUrl())
          .role(Role.USER)
          .build());
    }

    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(
        member.isPresent() ? member.get().getRole().getKey() : Role.USER.getKey()));

    return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "id");
  }

  private OAuthType getSocialProvider(OAuth2UserRequest userRequest) {
    return OAuthType.ofType(userRequest.getClientRegistration().getRegistrationId());
  }
}
