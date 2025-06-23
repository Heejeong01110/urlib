package com.heez.urlib.domain.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentUpdateRequest(

    @Schema(description = "댓글 내용", example = "정말 좋은 글이에요!", maxLength = 500)
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 500, message = "내용은 최대 500자까지 입력 가능합니다.")
    String content
) {

}
