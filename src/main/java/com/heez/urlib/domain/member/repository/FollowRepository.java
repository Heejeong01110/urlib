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

  boolean existsFollowByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

  Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

  @Query("""
      select m
        from Member m
        join Follow f on f.following.id = m.id
       where f.follower.id = :followerId
      """)
  Page<MemberSummaryProjection> findFollowingSummaryListByFollowerId(
      @Param("followerId") Long followerId,
      Pageable pageable);

  @Query("""
      select m
      from Member m
      join Follow f on f.follower.id = m.id
      where f.following.id = :followingId
      """)
  Page<MemberSummaryProjection> findFollowerSummaryListByFollowingId(
      @Param("followingId") Long followingId,
      Pageable pageable);
}
