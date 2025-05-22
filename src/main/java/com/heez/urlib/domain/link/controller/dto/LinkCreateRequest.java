package com.heez.urlib.domain.link.controller.dto;

import lombok.Builder;

@Builder
public record LinkCreateRequest(
    String title,
    String url
) {

}
