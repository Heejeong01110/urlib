package com.heez.urlib.domain.member.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import com.heez.urlib.domain.auth.model.OAuthType;
import com.heez.urlib.domain.member.controller.dto.MemberDetailResponse;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private MemberServiceImpl memberService;

  @Test
  void findMemberOrCreate_existingMember() {
    // given
    OAuthType oAuthType = OAuthType.valueOf("KAKAO");
    OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
        .oAuthType(oAuthType)
        .oAuthId("kakao_123456789asdfzxcv")
        .nickname("nickname")
        .imageUrl("https://image.url.co.kr")
        .email("existing@example.com")
        .build();

    Member existing = Member.builder()
        .id(1L)
        .email(new Email("existing@example.com"))
        .build();
    given(memberRepository.findMemberByOauthTypeAndIdentifier(oAuthType, userInfo.oAuthId()))
        .willReturn(Optional.of(existing));

    // when
    Member result = memberService.findMemberOrCreate(userInfo);

    // then
    assertThat(result).isSameAs(existing);
    then(memberRepository)
        .should().findMemberByOauthTypeAndIdentifier(oAuthType, userInfo.oAuthId());
    then(memberRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  void findMemberOrCreate_newMember() {
    // given
    OAuthType oAuthType = OAuthType.valueOf("KAKAO");
    OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
        .oAuthType(oAuthType)
        .oAuthId("kakao_123456789asdfzxcv")
        .nickname("nickname")
        .imageUrl("https://image.url.co.kr")
        .email("existing@example.com")
        .build();
    given(memberRepository.findMemberByOauthTypeAndIdentifier(oAuthType, userInfo.oAuthId()))
        .willReturn(Optional.empty());

    Member saved = Member.builder()
        .id(2L)
        .email(new Email("new@example.com"))
        .build();
    given(memberRepository.save(any(Member.class))).willReturn(saved);

    // when
    Member result = memberService.findMemberOrCreate(userInfo);

    // then
    assertThat(result).isSameAs(saved);
    then(memberRepository)
        .should().findMemberByOauthTypeAndIdentifier(oAuthType, userInfo.oAuthId());
    then(memberRepository).should().save(any(Member.class));
  }

  @Test
  void findById_existing() {
    // given
    Member member = Member.builder()
        .id(1L)
        .email(new Email("user@example.com"))
        .build();
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
    given(member.getId()).willReturn(memberId);
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
}
