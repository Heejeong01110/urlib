package com.heez.urlib.domain.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentCreateRequest(

    @Schema(description = "댓글 내용", example = "이 글 정말 유익하네요!")
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 500, message = "내용은 최대 500자까지 입력 가능합니다.")
    String content,

    @Schema(description = "부모 댓글 ID (없을 경우 null 입력으로 새 댓글 생성)", example = "42", nullable = true)
    Long parentCommentId
) {

}
