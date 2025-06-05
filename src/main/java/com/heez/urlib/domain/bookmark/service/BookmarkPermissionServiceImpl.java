package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkModifyException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkPermissionServiceImpl implements BookmarkPermissionService {

  public void isVisible(Bookmark bookmark, Optional<Long> viewerId) {
    boolean isOwner = viewerId.map(id -> bookmark.getMember().getId().equals(id)).orElse(false);
    if (!bookmark.isVisibleToOthers() && !isOwner) {
      throw new AccessDeniedBookmarkException();
    }
  }

  public void isEditable(Bookmark bookmark, Long viewerId) {
    if (!bookmark.getMember().getId().equals(viewerId)) {
      throw new AccessDeniedBookmarkModifyException();
    }
  }
}
