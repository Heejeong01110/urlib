package com.heez.urlib.domain.auth.service.dto;

import lombok.Builder;

@Builder
public record ReissueDto(
    String accessToken,
    String refreshToken) {

}
