package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.model.Bookmark;

public interface BookmarkPermissionService {

  public void isVisible(Bookmark bookmark, Long viewerId);

  public void isEditable(Bookmark bookmark, Long viewerId);
}
