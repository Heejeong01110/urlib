package com.heez.urlib.domain.link.controller.dto;

import com.heez.urlib.domain.link.model.Link;
import lombok.Builder;

@Builder
public record LinkDetailResponse(
    Long id,
    String title,
    String url
) {

  public static LinkDetailResponse from(Link link) {
    return new LinkDetailResponse(
        link.getLinkId(),
        link.getTitle(),
        link.getUrl()
    );
  }
}
