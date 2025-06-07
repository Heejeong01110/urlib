package com.heez.urlib.domain.auth.controller.dto;

import lombok.Builder;

@Builder
public record LoginRequest(
    String email,
    String password
) {

}
