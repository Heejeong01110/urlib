package com.heez.urlib.domain.bookmark.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "북마크 좋아요 응답 DTO")
@Builder
public record LikeResponse(
    
    @Schema(description = "좋아요 상태 (true: 좋아요 누름, false: 좋아요 안 누름)", example = "true")
    Boolean liked,

    @Schema(description = "현재 좋아요 수", example = "27")
    Long likeCount
) {

}
