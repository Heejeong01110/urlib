package com.heez.urlib.domain.bookmark.repository;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.service.dto.BookmarkSummaryProjection;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT b FROM Bookmark b WHERE b.bookmarkId = :bookmarkId")
  Optional<Bookmark> findByIdWithLock(
      @Param("bookmarkId") Long bookmarkId);

  @Query("""
        select b
          from Bookmark b
         where b.member.memberId = :memberId
           and ( :memberId = :viewerId or b.visibleToOthers )
      """)
  @EntityGraph(attributePaths = {"member"})
  Page<BookmarkSummaryProjection> findPageByMemberAndViewer(
      @Param("memberId") Long ownerId,
      @Param("viewerId") Long viewerId,
      Pageable pageable);

  @Query("""
        select b
          from Bookmark b
         where b.member.memberId = :memberId
           and b.visibleToOthers
      """)
  @EntityGraph(attributePaths = {"member"})
  Page<BookmarkSummaryProjection> findPageByMember(Long ownerId, Pageable pageable);

  @Query("""
        select b
          from Bookmark b
         where ( b.member.memberId = :viewerId or b.visibleToOthers )
      """)
  @EntityGraph(attributePaths = {"member"})
  Page<BookmarkSummaryProjection> findPageByViewer(
      @Param("viewerId") Long viewerId,
      Pageable pageable);

  @Query("""
        select b
          from Bookmark b
         where b.visibleToOthers
      """)
  @EntityGraph(attributePaths = {"member"})
  Page<BookmarkSummaryProjection> findPageByAnonymous(Pageable pageable);

  @Modifying
  @Query("UPDATE Bookmark b SET b.viewCount = b.viewCount + 1 WHERE b.bookmarkId = :bookmarkId")
  void incrementViewCount(@Param("bookmarkId") Long bookmarkId);

  @Modifying
  @Query("UPDATE Bookmark b SET b.likeCount = b.likeCount + 1 WHERE b.bookmarkId = :bookmarkId")
  void incrementLikeCount(@Param("bookmarkId") Long bookmarkId);

  @Modifying
  @Query("UPDATE Bookmark b SET b.likeCount = b.likeCount - 1 WHERE b.bookmarkId = :bookmarkId AND b.likeCount > 0")
  void decrementLikeCount(@Param("bookmarkId") Long bookmarkId);

}
