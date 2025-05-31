package com.heez.urlib.domain.member.repository;

import com.heez.urlib.domain.member.model.Follow;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  boolean existsFollowByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

  Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
