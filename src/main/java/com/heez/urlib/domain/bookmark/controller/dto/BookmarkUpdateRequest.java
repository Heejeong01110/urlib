package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "북마크 수정 요청 DTO")
public record BookmarkUpdateRequest(

    @Schema(description = "수정할 북마크 제목", example = "Updated Spring 링크 모음")
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    String title,

    @Schema(description = "수정할 북마크 설명", example = "업데이트된 스프링 자료들 모음")
    @Size(max = 500, message = "설명은 최대 500자까지 입력 가능합니다.")
    String description,

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.png")
    String imageUrl,

    @Schema(description = "공개 여부", example = "true")
    Boolean visibleToOthers,

    @Schema(description = "수정할 태그 리스트", example = "[\"Spring\", \"Java\"]")
    List<String> tags,

    @Schema(description = "수정할 링크 리스트")
    List<BaseLinkRequest> links

) {

}
