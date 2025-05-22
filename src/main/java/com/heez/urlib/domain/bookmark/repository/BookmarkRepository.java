package com.heez.urlib.domain.bookmark.repository;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  boolean existsByBookmarkIdAndMember_Id(
      @Param("bookmarkId") Long bookmarkId,
      @Param("id") Long memberId);
}
