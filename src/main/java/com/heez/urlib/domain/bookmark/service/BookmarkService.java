package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkUpdateRequest;

public interface BookmarkService {

  BookmarkCreateResponse createBookmark(Long memberId, BookmarkCreateRequest req);

  BookmarkDetailResponse getBookmark(Long memberId, Long bookmarkId);

  BookmarkDetailResponse updateBookmark(Long memberId, Long bookmarkId, BookmarkUpdateRequest request);
}
