package com.heez.urlib.domain.link.controller.dto;

import com.heez.urlib.domain.link.model.Link;
import lombok.Builder;

@Builder
public record LinkCreateResponse(
    String title,
    String url
) {
  public static LinkCreateResponse from(Link link){
    return new LinkCreateResponse(
        link.getTitle(),
        link.getUrl()
    );
  }
}
