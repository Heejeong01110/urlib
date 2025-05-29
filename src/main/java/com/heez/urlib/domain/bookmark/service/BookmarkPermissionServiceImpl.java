package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkModifyException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkPermissionServiceImpl implements BookmarkPermissionService{

  private final BookmarkRepository bookmarkRepository;

  public void isVisible(Bookmark bookmark, Long viewerId) {
    if (bookmarkRepository.existsByBookmarkIdAndMember_Id(bookmark.getBookmarkId(), viewerId)
        || !bookmark.isVisibleToOthers()) {
      throw new AccessDeniedBookmarkException();
    }
  }

  public void isEditable(Bookmark bookmark, Long viewerId) {
    if (!bookmark.getMember().getId().equals(viewerId)) {
      throw new AccessDeniedBookmarkModifyException();
    }
  }
}
