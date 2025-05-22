package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.link.controller.dto.LinkCreateResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record BookmarkCreateResponse(
    Long id,
    String title,
    String description,
    String imageUrl,
    Boolean visibleToOthers,
    List<String> tags,
    List<LinkCreateResponse> links
) {
  public static BookmarkCreateResponse from(Bookmark bookmark) {
    return new BookmarkCreateResponse(
        bookmark.getBookmarkId(),
        bookmark.getTitle(),
        bookmark.getDescription(),
        bookmark.getImageUrl(),
        bookmark.isVisibleToOthers(),
        bookmark.getBookmarkHashtags().stream().map((tag) -> tag.getHashtag().getTitle()).toList(),
        bookmark.getLinks().stream().map(LinkCreateResponse::from).toList()
    );
  }
}
