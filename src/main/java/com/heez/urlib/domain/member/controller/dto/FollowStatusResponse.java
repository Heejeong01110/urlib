package com.heez.urlib.domain.member.controller.dto;

import lombok.Builder;

@Builder
public record FollowStatusResponse(
    Boolean follow
) {

}
