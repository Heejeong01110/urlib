package com.heez.urlib.domain.tag.repository;

import com.heez.urlib.domain.tag.model.BookmarkHashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkHashtagRepository extends JpaRepository<BookmarkHashtag, Long> {
  @Query("""
      SELECT bh.hashtag.name
      FROM BookmarkHashtag bh
      WHERE bh.bookmark.bookmarkId = :bookmarkId
    """)
  List<String> findTagNamesByBookmarkId(@Param("bookmarkId") Long bookmarkId);
}
