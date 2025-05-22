package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.link.controller.dto.LinkCreateResponse;
import com.heez.urlib.domain.link.controller.dto.LinkDetailResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record BookmarkDetailResponse(
    Long id,
    String title,
    String description,
    String imageUrl,
    Boolean visibleToOthers,
    Long viewCount,
    LocalDateTime createdAt,
    List<String> tags,
    List<LinkCreateResponse> links,
    Long writerId
) {

  public static BookmarkDetailResponse from(Bookmark entity, List<String> tags,
      List<LinkDetailResponse> links, Long memberId) {
    return new BookmarkDetailResponse(
        entity.getBookmarkId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getImageUrl(),
        entity.isVisibleToOthers(),
        entity.getViewCount(),
        entity.getCreatedAt(),
        tags,
        links,
        memberId
    );
  }
}
