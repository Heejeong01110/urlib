package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.controller.dto.LikeResponse;

public interface BookmarkLikeService {

  public LikeResponse likeBookmark(Long memberId, Long bookmarkId);

  public LikeResponse unlikeBookmark(Long memberId, Long bookmarkId);
}
