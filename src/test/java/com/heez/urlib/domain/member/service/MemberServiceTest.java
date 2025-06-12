package com.heez.urlib.domain.member.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private MemberService memberService;

  @Test
  void findMemberOrCreate_existingMember_returnMember() {
    // given
    AuthType authType = AuthType.valueOf("KAKAO");
    OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
        .authType(authType)
        .oAuthId("kakao_123456789asdfzxcv")
        .nickname("nickname")
        .imageUrl("https://image.url.co.kr")
        .email("existing@example.com")
        .build();

    Member existing = Member.builder()
        .email(new Email("existing@example.com"))
        .build();
    ReflectionTestUtils.setField(existing, "memberId", 1L);
    given(memberRepository.findMemberByOauthTypeAndIdentifier(authType, userInfo.oAuthId()))
        .willReturn(Optional.of(existing));

    // when
    Member result = memberService.findMemberOrCreate(userInfo);

    // then
    assertThat(result).isSameAs(existing);
    then(memberRepository)
        .should().findMemberByOauthTypeAndIdentifier(authType, userInfo.oAuthId());
    then(memberRepository).should(never()).save(any(Member.class));
  }

  @Test
  void findMemberOrCreate_newMemberCreated_success() {
    // given
    AuthType authType = AuthType.KAKAO;
    OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
        .authType(authType)
        .oAuthId("kakao_123456789asdfzxcv")
        .nickname("nickname")
        .imageUrl("https://image.url.co.kr")
        .email("existing@example.com")
        .build();
    Member expected = Member.builder()
        .oauthType(authType)
        .identifier(userInfo.oAuthId())
        .email(new Email(userInfo.email()))
        .nickname(new Nickname(authType.getProvider() + "_12345678")) // 예시값, 실제 닉네임 생성 로직에 맞게 수정
        .imageUrl(userInfo.imageUrl())
        .role(Role.USER)
        .build();
    ReflectionTestUtils.setField(expected, "memberId", 2L);

    given(memberRepository.findMemberByOauthTypeAndIdentifier(authType, userInfo.oAuthId()))
        .willReturn(Optional.empty());
    given(memberRepository.existsByNickname(any())).willReturn(false);
    given(memberRepository.save(any(Member.class))).willReturn(expected);

    // when
    Member result = memberService.findMemberOrCreate(userInfo);

    // then
    assertThat(result).isSameAs(expected);
    then(memberRepository).should()
        .findMemberByOauthTypeAndIdentifier(authType, userInfo.oAuthId());
    then(memberRepository).should().save(any(Member.class));
  }

  @Test
  void findMemberOrCreate_nicknameGenerationFails_throwException() {
    // given
    AuthType authType = AuthType.KAKAO;
    OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
        .authType(authType)
        .oAuthId("kakao_123456789asdfzxcv")
        .nickname("nickname")
        .imageUrl("https://image.url.co.kr")
        .email("existing@example.com")
        .build();

    given(memberRepository.findMemberByOauthTypeAndIdentifier(userInfo.authType(), userInfo.oAuthId()))
        .willReturn(Optional.empty());
    given(memberRepository.existsByNickname(any(Nickname.class))).willReturn(true);

    // when / then
    assertThatThrownBy(() -> memberService.findMemberOrCreate(userInfo))
        .isInstanceOf(NicknameAutoGenerationFailedException.class);

    then(memberRepository).should(never()).save(any(Member.class));
  }

  @Test
  void findById_existing() {
    // given
    Member member = Member.builder()
        .email(new Email("user@example.com"))
        .build();
    ReflectionTestUtils.setField(member, "memberId", 1L);
    given(memberRepository.findById(1L)).willReturn(Optional.of(member));

    // when
    Member result = memberService.findById(1L);

    // then
    assertThat(result).isSameAs(member);
    then(memberRepository).should().findById(1L);
  }

  @Test
  void findById_notFound_throws() {
    // given
    given(memberRepository.findById(1L)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> memberService.findById(1L))
        .isInstanceOf(MemberNotFoundException.class);
    then(memberRepository).should().findById(1L);
  }

  @Test
  void getProfile_existingMember_returnsDetail() {
    // given
    Long memberId = 1L;
    Member member = org.mockito.Mockito.mock(Member.class);
    given(member.getMemberId()).willReturn(memberId);
    given(member.getImageUrl()).willReturn("http://example.com/profile.png");
    given(member.getDescription()).willReturn("Profile description");
    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

    // when
    MemberDetailResponse response = memberService.getProfile(memberId);

    // then
    assertThat(response).isNotNull();
    assertThat(response.memberId()).isEqualTo(memberId);
    assertThat(response.imageUrl()).isEqualTo("http://example.com/profile.png");
    assertThat(response.description()).isEqualTo("Profile description");
  }

  @Test
  void getProfile_missingMember_throwsException() {
    // given
    Long missingId = 99L;
    given(memberRepository.findById(missingId)).willReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> memberService.getProfile(missingId))
        .isInstanceOf(MemberNotFoundException.class);
  }

  @Test
  void findByEmail_existing() {
    // given
    Email email = new Email("user@example.com");
    Member member = Member.builder()
        .email(email)
        .build();
    given(memberRepository.findMemberByEmail(any(Email.class))).willReturn(Optional.of(member));

    // when
    Member result = memberService.findByEmail("user@example.com");

    // then
    assertThat(result).isSameAs(member);
    then(memberRepository).should().findMemberByEmail(any(Email.class));
  }

  @Test
  void findByEmail_notFound_throws() {
    // given
    Email email = new Email("user@example.com");
    given(memberRepository.findMemberByEmail(any(Email.class))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> memberService.findByEmail("user@example.com"))
        .isInstanceOf(MemberNotFoundException.class);
    then(memberRepository).should().findMemberByEmail(any(Email.class));
  }

}
