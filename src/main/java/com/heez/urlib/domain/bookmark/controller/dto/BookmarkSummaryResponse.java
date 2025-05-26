package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.service.dto.BookmarkSummaryProjection;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;

public record BookmarkSummaryResponse(
    Long id,
    String title,
    String description,
    String bookmarkImageUrl,
    MemberSummaryResponse memberSummary
) {

  public static BookmarkSummaryResponse from(BookmarkSummaryProjection bookmark) {
    return new BookmarkSummaryResponse(
        bookmark.getBookmarkId(),
        bookmark.getTitle(),
        bookmark.getDescription(),
        bookmark.getImageUrl(),
        new MemberSummaryResponse(
            bookmark.getMember().getId(),
            bookmark.getMember().getImageUrl()
        )
    );
  }
}
