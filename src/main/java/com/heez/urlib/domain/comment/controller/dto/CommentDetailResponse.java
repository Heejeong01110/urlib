package com.heez.urlib.domain.comment.controller.dto;

import com.heez.urlib.domain.comment.model.Comment;
import com.heez.urlib.domain.comment.service.dto.CommentDetailProjection;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import com.heez.urlib.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentDetailResponse(

    @Schema(description = "댓글 ID", example = "42")
    Long id,

    @Schema(description = "댓글 내용", example = "정말 좋은 글이에요!", maxLength = 500)
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 500, message = "내용은 최대 500자까지 입력 가능합니다.")
    String content,

    @Schema(description = "댓글 작성자 요약 정보")
    MemberSummaryResponse memberInfo
) {

  public static CommentDetailResponse from(CommentDetailProjection comment) {
    return new CommentDetailResponse(
        comment.getCommentId(),
        comment.getContent(),
        new MemberSummaryResponse(
            comment.getMember().getId(),
            comment.getMember().getImageUrl()
        )
    );
  }


  public static CommentDetailResponse from(Comment comment, Member member) {
    return new CommentDetailResponse(
        comment.getCommentId(),
        comment.getContent(),
        new MemberSummaryResponse(
            comment.getMember().getMemberId(),
            comment.getMember().getImageUrl()
        )
    );
  }
}
