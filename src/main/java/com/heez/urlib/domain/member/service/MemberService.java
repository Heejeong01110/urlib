package com.heez.urlib.domain.member.service;

import com.heez.urlib.domain.auth.exception.NicknameAutoGenerationFailedException;
import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import com.heez.urlib.domain.member.controller.dto.MemberDetailResponse;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.model.vo.Nickname;
import com.heez.urlib.domain.member.repository.MemberRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional
  public Member findMemberOrCreate(OAuth2UserInfo userInfo) {
    return memberRepository
        .findMemberByOauthTypeAndIdentifier(userInfo.authType(), userInfo.oAuthId())
        .orElseGet(() -> memberRepository.save(createMemberFrom(userInfo)));
  }

  public MemberDetailResponse getProfile(Long memberId) {
    return MemberDetailResponse.from(memberRepository.findById(memberId)
        .orElseThrow(MemberNotFoundException::new));
  }

  public Member findById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(MemberNotFoundException::new);
  }

  private Member createMemberFrom(OAuth2UserInfo userInfo) {
    return Member.builder()
        .oauthType(userInfo.authType())
        .identifier(userInfo.oAuthId())
        .email(new Email(userInfo.email()))
        .nickname(new Nickname(generateUniqueNickname(userInfo.authType())))
        .imageUrl(userInfo.imageUrl())
        .role(Role.USER)
        .build();
  }

  public String generateUniqueNickname(AuthType authType) {
    String base = authType.getProvider() + "_";
    String nickname = base + UUID.randomUUID().toString().substring(0, 8);
    int retry = 0;
    while (memberRepository.existsByNickname(new Nickname(nickname))) {
      retry++;
      if (retry > 10) {
        throw new NicknameAutoGenerationFailedException();
      }
      nickname = base + UUID.randomUUID().toString().substring(0, 6);
    }
    return nickname;
  }

  public Member findByEmail(String email) {
    return memberRepository.findMemberByEmail(new Email(email))
        .orElseThrow(MemberNotFoundException::new);
  }
}
