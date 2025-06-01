package com.heez.urlib.domain.member.service;

import com.heez.urlib.domain.member.controller.dto.FollowStatusResponse;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {

  void follow(Long followingId, Long followerId);

  void unfollow(Long followingId, Long followerId);

  FollowStatusResponse getFollowStatus(Long followingId, Long followerId);

  Page<MemberSummaryResponse> getFollowerList(Long memberId, Pageable pageable);

  Page<MemberSummaryResponse> getFollowingList(Long memberId, Pageable pageable);
}
