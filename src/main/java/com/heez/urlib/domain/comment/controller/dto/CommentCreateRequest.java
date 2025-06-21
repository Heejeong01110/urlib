package com.heez.urlib.domain.comment.controller.dto;

import lombok.Builder;

@Builder
public record CommentCreateRequest(
    String content,
    Long parentCommentId
) {

}
