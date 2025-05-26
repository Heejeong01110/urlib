package com.heez.urlib.domain.link.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BaseLinkRequest(
    @NotBlank
    String title,
    @NotBlank
    String url
) {

}
