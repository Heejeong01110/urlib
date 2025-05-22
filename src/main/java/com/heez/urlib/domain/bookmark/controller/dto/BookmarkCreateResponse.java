package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.link.controller.dto.LinkCreateResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record BookmarkCreateResponse(
    Long id,
    String name,
    String description,
    String imageUrl,
    Boolean isPublic,
    List<String> tags,
    List<LinkCreateResponse> links
) {
  public static BookmarkCreateResponse from(Bookmark bookmark) {
    return new BookmarkCreateResponse(
        bookmark.getBookmarkId(),
        bookmark.getName(),
        bookmark.getDescription(),
        bookmark.getImageUrl(),
        bookmark.isPublic(),
        bookmark.getBookmarkHashtags().stream().map((tag) -> tag.getHashtag().getName()).toList(),
        bookmark.getLinks().stream().map(LinkCreateResponse::from).toList()
    );
  }
}
