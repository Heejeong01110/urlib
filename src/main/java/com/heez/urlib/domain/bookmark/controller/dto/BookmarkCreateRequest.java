package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Schema(description = "북마크 생성 요청 DTO")
@Builder
public record BookmarkCreateRequest(
    @Schema(description = "북마크 제목", example = "나중에 읽을 기사")
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    String title,

    @Schema(description = "북마크 설명", example = "중요한 내용 요약")
    @Size(max = 500, message = "설명은 최대 500자까지 입력 가능합니다.")
    String description,

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.png")
    String imageUrl,

    @Schema(description = "공개 여부", example = "true")
    Boolean visibleToOthers,

    @Schema(description = "북마크에 포함될 태그 리스트", example = "[\"뉴스\", \"기술\"]")
    List<String> tags,

    @Schema(description = "북마크에 포함될 링크 목록")
    List<BaseLinkRequest> links
) {

}
