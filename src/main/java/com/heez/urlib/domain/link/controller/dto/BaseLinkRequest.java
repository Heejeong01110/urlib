package com.heez.urlib.domain.link.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Schema(description = "링크 Base 요청 DTO")
@Builder
public record BaseLinkRequest(
    @Schema(description = "링크 ID (수정 시 사용)", example = "101", nullable = true)
    Long id,

    @Schema(description = "링크 제목", example = "google 홈페이지")
    @NotBlank
    String title,

    @Schema(description = "링크 URL", example = "https://google.com")
    @NotBlank
    String url

) {

}
