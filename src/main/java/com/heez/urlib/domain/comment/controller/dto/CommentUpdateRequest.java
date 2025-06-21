package com.heez.urlib.domain.comment.controller.dto;

import lombok.Builder;

@Builder
public record CommentUpdateRequest(
    String content
) {

}
