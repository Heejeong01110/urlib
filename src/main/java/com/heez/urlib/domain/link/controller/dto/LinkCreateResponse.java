package com.heez.urlib.domain.link.controller.dto;

import com.heez.urlib.domain.link.model.Link;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "링크 생성 응답 DTO")
@Builder
public record LinkCreateResponse(
    @Schema(description = "생성된 링크 ID", example = "101")
    Long id,

    @Schema(description = "링크 제목", example = "google 홈페이지")
    String title,

    @Schema(description = "링크 URL", example = "https://google.com")
    String url

) {

  public static LinkCreateResponse from(Link link) {
    return new LinkCreateResponse(
        link.getLinkId(),
        link.getTitle(),
        link.getUrl()
    );
  }
}
