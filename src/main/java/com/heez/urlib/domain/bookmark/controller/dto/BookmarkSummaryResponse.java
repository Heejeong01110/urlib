package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.service.dto.BookmarkSummaryProjection;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "북마크 요약 응답 DTO")
public record BookmarkSummaryResponse(

    @Schema(description = "북마크 ID", example = "42")
    Long id,

    @Schema(description = "북마크 제목", example = "Spring 공부 링크 모음")
    String title,

    @Schema(description = "북마크 설명", example = "자주 보는 스프링 관련 블로그 정리")
    String description,

    @Schema(description = "북마크 대표 이미지 URL", example = "https://example.com/image.png")
    String bookmarkImageUrl,

    @Schema(description = "북마크 작성자 요약 정보")
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
