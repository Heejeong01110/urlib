package com.heez.urlib.domain.bookmark.repository;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.service.dto.BookmarkSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  boolean existsByBookmarkIdAndMember_Id(
      @Param("bookmarkId") Long bookmarkId,
      @Param("id") Long memberId);

  @Query("""
        select b
          from Bookmark b
         where b.member.id = :memberId
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
         where b.member.id = :memberId
           and b.visibleToOthers
      """)
  @EntityGraph(attributePaths = {"member"})
  Page<BookmarkSummaryProjection> findPageByMember(Long ownerId, Pageable pageable);

  @Query("""
        select b
          from Bookmark b
         where ( b.member.id = :viewerId or b.visibleToOthers )
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

}
