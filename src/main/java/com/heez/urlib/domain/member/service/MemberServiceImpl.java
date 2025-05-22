package com.heez.urlib.domain.member.service;

import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.model.vo.Nickname;
import com.heez.urlib.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{

  private final MemberRepository memberRepository;

  @Transactional
  public Member findMemberOrCreate(OAuth2UserInfo userInfo) {
    return memberRepository
        .findMemberByOauthTypeAndIdentifier(userInfo.oAuthType(), userInfo.oAuthId())
        .orElseGet(() -> memberRepository.save(createMemberFrom(userInfo)));
  }

  public Member findById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(MemberNotFoundException::new);
  }

  private Member createMemberFrom(OAuth2UserInfo userInfo) {
    return Member.builder()
        .oauthType(userInfo.oAuthType())
        .identifier(userInfo.oAuthId())
        .email(new Email(userInfo.email()))
        .nickname(new Nickname(userInfo.nickname()))
        .imageUrl(userInfo.imageUrl())
        .role(Role.USER)
        .build();
  }

}
