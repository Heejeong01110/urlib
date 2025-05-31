package com.heez.urlib.domain.member.service;

public interface FollowService {

  void follow(Long followingId, Long followerId);

  void unfollow(Long followingId, Long followerId);

}
