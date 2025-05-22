package com.heez.urlib.domain.link.repository;

import com.heez.urlib.domain.link.model.Link;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {
  List<Link> findAllByBookmark_BookmarkId(Long bookmarkId);
}
