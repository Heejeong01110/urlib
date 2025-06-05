package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import java.util.Optional;

public interface BookmarkPermissionService {

  public void isVisible(Bookmark bookmark, Optional<Long> viewerId);

  public void isEditable(Bookmark bookmark, Long viewerId);
}
