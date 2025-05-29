package com.heez.urlib.domain.bookmark.controller.dto;

import lombok.Builder;

@Builder
public record LikeResponse(
    Boolean liked,
    Long likeCount
) {

}
