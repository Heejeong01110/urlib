package com.heez.urlib.domain.member.controller.dto;

import com.heez.urlib.domain.member.model.Member;

public record MemberDetailResponse(
    Long memberId,
    String imageUrl,
    String description
) {

  public static MemberDetailResponse from(Member member) {
    return new MemberDetailResponse(
        member.getId(),
        member.getImageUrl(),
        member.getDescription()
    );
  }
}
