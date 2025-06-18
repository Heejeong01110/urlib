package com.heez.urlib.domain.link.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BaseLinkRequest(
    Long id,
    @NotBlank
    String title,
    @NotBlank
    String url
) {

}
