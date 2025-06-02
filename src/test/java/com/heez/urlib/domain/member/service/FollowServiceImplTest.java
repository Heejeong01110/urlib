package com.heez.urlib.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.heez.urlib.domain.member.controller.dto.FollowStatusResponse;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import com.heez.urlib.domain.member.exception.AlreadyFollowingException;
import com.heez.urlib.domain.member.exception.NotFollowingException;
import com.heez.urlib.domain.member.exception.SelfFollowException;
import com.heez.urlib.domain.member.model.Follow;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.repository.FollowRepository;
import com.heez.urlib.domain.member.service.dto.MemberSummaryProjection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

  @Mock
  private FollowRepository followRepository;

  @Mock
  private MemberServiceImpl memberService;

  @InjectMocks
  private FollowServiceImpl followService;

  @Test
  void follow_validMembers_savesFollow() {
    // given
    Long followingId = 1L;
    Long followerId = 2L;

    Member followingMember = mock(Member.class);
    Member followerMember = mock(Member.class);

    given(memberService.findById(followingId)).willReturn(followingMember);
    given(memberService.findById(followerId)).willReturn(followerMember);
    given(followRepository.existsFollowByFollower_IdAndFollowing_Id(followerId,
        followingId)).willReturn(false);

    // when
    followService.follow(followingId, followerId);

    // then
    ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);
    verify(followRepository, times(1)).save(captor.capture());

    Follow savedFollow = captor.getValue();
    assertThat(savedFollow.getFollowing()).isEqualTo(followingMember);
    assertThat(savedFollow.getFollower()).isEqualTo(followerMember);
  }

  @Test
  void follow_selfFollow_throwsSelfFollowException() {
    // given
    Long sameId = 5L;

    // when & then
    assertThatThrownBy(() -> followService.follow(sameId, sameId))
        .isInstanceOf(SelfFollowException.class);
  }

  @Test
  void follow_alreadyFollowing_throwsAlreadyFollowingException() {
    // given
    Long followingId = 1L;
    Long followerId = 2L;

    Member followingMember = mock(Member.class);
    Member followerMember = mock(Member.class);

    given(memberService.findById(followingId)).willReturn(followingMember);
    given(memberService.findById(followerId)).willReturn(followerMember);
    given(followRepository.existsFollowByFollower_IdAndFollowing_Id(followerId,
        followingId)).willReturn(true);

    // when & then
    assertThatThrownBy(() -> followService.follow(followingId, followerId))
        .isInstanceOf(AlreadyFollowingException.class);
  }

  @Test
  void unfollow_validMembers_deletesFollow() {
    // given
    Long followingId = 1L;
    Long followerId = 2L;

    Member followingMember = mock(Member.class);
    Member followerMember = mock(Member.class);
    Follow existingFollow = mock(Follow.class);

    given(memberService.findById(followingId)).willReturn(followingMember);
    given(memberService.findById(followerId)).willReturn(followerMember);
    given(followRepository.findByFollowerIdAndFollowingId(followerId, followingId))
        .willReturn(Optional.of(existingFollow));

    // when
    followService.unfollow(followingId, followerId);

    // then
    verify(followRepository, times(1)).delete(existingFollow);
  }

  @Test
  void unfollow_selfFollow_throwsSelfFollowException() {
    // given
    Long sameId = 5L;

    // when & then
    assertThatThrownBy(() -> followService.unfollow(sameId, sameId))
        .isInstanceOf(SelfFollowException.class);
  }

  @Test
  void unfollow_notFollowing_throwsNotFollowingException() {
    // given
    Long followingId = 1L;
    Long followerId = 2L;

    Member followingMember = mock(Member.class);
    Member followerMember = mock(Member.class);

    given(memberService.findById(followingId)).willReturn(followingMember);
    given(memberService.findById(followerId)).willReturn(followerMember);
    given(followRepository.findByFollowerIdAndFollowingId(followerId, followingId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> followService.unfollow(followingId, followerId))
        .isInstanceOf(NotFollowingException.class);
  }

  @Test
  void getFollowStatus_whenFollowingExists_returnsTrue() {
    // given
    Long followingId = 1L;
    Long followerId = 2L;
    Follow existingFollow = org.mockito.Mockito.mock(Follow.class);
    given(followRepository.findByFollowerIdAndFollowingId(followerId, followingId))
        .willReturn(Optional.of(existingFollow));

    // when
    FollowStatusResponse response = followService.getFollowStatus(followingId, followerId);

    // then
    assertThat(response.follow()).isTrue();
  }

  @Test
  void getFollowStatus_whenNoFollow_returnsFalse() {
    // given
    Long followingId = 1L;
    Long followerId = 2L;
    given(followRepository.findByFollowerIdAndFollowingId(followerId, followingId))
        .willReturn(Optional.empty());

    // when
    FollowStatusResponse response = followService.getFollowStatus(followingId, followerId);

    // then
    assertThat(response.follow()).isFalse();
  }

  @Test
  void getFollowerList_returnsMappedPage() {
    // given
    Long memberId = 10L;
    Pageable pageable = PageRequest.of(0, 2);
    MemberSummaryProjection memberA = mock(MemberSummaryProjection.class);
    MemberSummaryProjection memberB = mock(MemberSummaryProjection.class);
    given(memberA.getId()).willReturn(100L);
    given(memberB.getId()).willReturn(200L);
    // Stub any fields used by MemberSummaryResponse.from(), e.g. nickname or imageUrl if necessary
    given(memberA.getImageUrl()).willReturn("http://example.com/imageA.png");
    given(memberB.getImageUrl()).willReturn("http://example.com/imageB.png");

    Page<MemberSummaryProjection> mockPage = new PageImpl<>(List.of(memberA, memberB), pageable, 2);
    given(followRepository.findFollowerSummaryListByFollowingId(memberId, pageable))
        .willReturn(mockPage);

    // when
    Page<MemberSummaryResponse> result = followService.getFollowerList(memberId, pageable);

    // then
    verify(followRepository).findFollowerSummaryListByFollowingId(memberId, pageable);
    assertThat(result.getTotalElements()).isEqualTo(2);
    List<MemberSummaryResponse> content = result.getContent();
    assertThat(content.get(0).memberId()).isEqualTo(100L);
    assertThat(content.get(1).memberId()).isEqualTo(200L);
  }

  @Test
  void getFollowingList_returnsMappedPage() {
    // given
    Long memberId = 20L;
    Pageable pageable = PageRequest.of(0, 3);

    MemberSummaryProjection memberX = mock(MemberSummaryProjection.class);
    MemberSummaryProjection memberY = mock(MemberSummaryProjection.class);
    given(memberX.getId()).willReturn(300L);
    given(memberY.getId()).willReturn(400L);
    // Stub any fields used by MemberSummaryResponse.from()
    given(memberX.getImageUrl()).willReturn("http://example.com/imageX.png");
    given(memberY.getImageUrl()).willReturn("http://example.com/imageY.png");

    Page<MemberSummaryProjection> mockPage = new PageImpl<>(List.of(memberX, memberY), pageable, 2);
    given(followRepository.findFollowingSummaryListByFollowerId(memberId, pageable))
        .willReturn(mockPage);

    // when
    Page<MemberSummaryResponse> result = followService.getFollowingList(memberId, pageable);

    // then
    verify(followRepository).findFollowingSummaryListByFollowerId(memberId, pageable);
    assertThat(result.getTotalElements()).isEqualTo(2);
    List<MemberSummaryResponse> content = result.getContent();
    assertThat(content.get(0).memberId()).isEqualTo(300L);
    assertThat(content.get(1).memberId()).isEqualTo(400L);
  }

}
