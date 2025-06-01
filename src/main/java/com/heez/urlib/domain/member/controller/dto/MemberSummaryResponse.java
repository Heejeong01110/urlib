package com.heez.urlib.domain.member.controller.dto;

import com.heez.urlib.domain.member.service.dto.MemberSummaryProjection;

public record MemberSummaryResponse(
    Long memberId,
    String memberImageUrl
) {

  public static MemberSummaryResponse from(MemberSummaryProjection projection) {
    return new MemberSummaryResponse(
        projection.getId(),
        projection.getImageUrl()
    );
  }

}
