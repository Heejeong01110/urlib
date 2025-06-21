package com.heez.urlib.domain.comment.controller.dto;

import com.heez.urlib.domain.comment.model.Comment;
import com.heez.urlib.domain.comment.service.dto.CommentDetailProjection;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import com.heez.urlib.domain.member.model.Member;
import lombok.Builder;

@Builder
public record CommentDetailResponse(
    Long id,
    String content,
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
