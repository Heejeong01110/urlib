package com.heez.urlib.domain.member.repository;

import com.heez.urlib.domain.member.model.Follow;
import com.heez.urlib.domain.member.service.dto.MemberSummaryProjection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  boolean existsFollowByFollower_MemberIdAndFollowing_MemberId(Long followerId, Long followingId);

  Optional<Follow> findByFollowerMemberIdAndFollowingMemberId(Long followerId, Long followingId);

  @Query("""
      select m
        from Member m
        join Follow f on f.following.memberId = m.memberId
       where f.follower.memberId = :followerId
      """)
  Page<MemberSummaryProjection> findFollowingSummaryListByFollowerId(
      @Param("followerId") Long followerId,
      Pageable pageable);

  @Query("""
      select m
      from Member m
      join Follow f on f.follower.memberId = m.memberId
      where f.following.memberId = :followingId
      """)
  Page<MemberSummaryProjection> findFollowerSummaryListByFollowingId(
      @Param("followingId") Long followingId,
      Pageable pageable);
}
