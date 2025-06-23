package com.heez.urlib.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "팔로우 상태 조회 DTO")
@Builder
public record FollowStatusResponse(
    @Schema(description = "팔로우 상태", example = "false")
    Boolean follow
) {

}
