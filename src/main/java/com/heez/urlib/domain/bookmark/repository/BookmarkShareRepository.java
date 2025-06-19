package com.heez.urlib.domain.bookmark.repository;

import com.heez.urlib.domain.bookmark.model.BookmarkShare;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkShareRepository extends JpaRepository<BookmarkShare, Long> {

  Optional<BookmarkShare> findByBookmark_BookmarkIdAndMember_MemberId(
      @Param("bookmarkId") Long bookmarkId,
      @Param("id") Long memberId);
}
