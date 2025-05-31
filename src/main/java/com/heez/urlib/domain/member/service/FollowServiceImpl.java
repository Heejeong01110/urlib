package com.heez.urlib.domain.member.service;


import com.heez.urlib.domain.member.exception.AlreadyFollowingException;
import com.heez.urlib.domain.member.exception.NotFollowingException;
import com.heez.urlib.domain.member.exception.SelfFollowException;
import com.heez.urlib.domain.member.model.Follow;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

  private final FollowRepository followRepository;

  private final MemberService memberService;

  @Override
  @Transactional
  public void follow(Long followingId, Long followerId) {
    isSelfFollow(followingId, followerId);
    Member following = memberService.findById(followingId);
    Member follower = memberService.findById(followerId);

    if (followRepository.existsFollowByFollower_IdAndFollowing_Id(followerId, followingId)) {
      throw new AlreadyFollowingException();
    }

    followRepository.save(Follow.builder()
        .follower(follower)
        .following(following)
        .build());
  }

  @Override
  @Transactional
  public void unfollow(Long followingId, Long followerId) {
    isSelfFollow(followingId, followerId);
    memberService.findById(followingId);
    memberService.findById(followerId);

    Follow existing = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
        .orElseThrow(NotFollowingException::new);

    followRepository.delete(existing);
  }

  private void isSelfFollow(Long followingId, Long followerId) {
    if (followingId.equals(followerId)) {
      throw new SelfFollowException();
    }
  }

}
