package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.link.controller.dto.LinkCreateResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "북마크 생성 응답 DTO")
@Builder
public record BookmarkCreateResponse(

    @Schema(description = "북마크 ID", example = "42")
    Long id,

    @Schema(description = "북마크 제목", example = "개발 참고 링크")
    String title,

    @Schema(description = "북마크 설명", example = "나중에 다시 볼 기술 블로그들")
    String description,

    @Schema(description = "북마크 대표 이미지 URL", example = "https://example.com/image.png")
    String imageUrl,

    @Schema(description = "공개 여부", example = "true")
    Boolean visibleToOthers,

    @Schema(description = "북마크에 포함된 태그 리스트", example = "[\"Spring\", \"OAuth\"]")
    List<String> tags,

    @Schema(description = "북마크에 포함된 링크 리스트")
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
