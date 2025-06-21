package com.heez.urlib.domain.comment.service.dto;

public interface CommentDetailProjection {

  Long getCommentId();

  String getContent();

  MemberInfo getMember();

  interface MemberInfo {

    Long getId();

    String getImageUrl();
  }
}
