package com.heez.urlib.domain.bookmark.repository;

import com.heez.urlib.domain.bookmark.model.BookmarkLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkLikeRepository extends JpaRepository<BookmarkLike, Long> {

  Optional<BookmarkLike> findByBookmark_BookmarkIdAndMember_MemberId(Long bookmarkId, Long memberId);
}
