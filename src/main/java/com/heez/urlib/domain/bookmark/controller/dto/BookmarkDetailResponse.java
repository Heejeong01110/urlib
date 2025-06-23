package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.link.controller.dto.LinkDetailResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Schema(description = "북마크 상세정보 DTO")
@Builder
public record BookmarkDetailResponse(

    @Schema(description = "북마크 ID", example = "42")
    Long id,

    @Schema(description = "북마크 제목", example = "Spring 공부 정리")
    String title,

    @Schema(description = "북마크 설명", example = "Spring 관련 블로그 모음")
    String description,

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.png")
    String imageUrl,

    @Schema(description = "공개 여부", example = "true")
    Boolean visibleToOthers,

    @Schema(description = "조회수", example = "123")
    Long viewCount,

    @Schema(description = "생성일", example = "2024-12-01T12:34:56")
    LocalDateTime createdAt,

    @Schema(description = "수정일", example = "2024-12-02T15:20:00")
    LocalDateTime updatedAt,

    @Schema(description = "태그 리스트", example = "[\"Java\", \"Spring\"]")
    List<String> tags,

    @Schema(description = "링크 상세 정보 리스트")
    List<LinkDetailResponse> links,

    @Schema(description = "작성자 회원 ID", example = "7")
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
        entity.getUpdatedAt(),
        tags,
        links,
        memberId
    );
  }
}
