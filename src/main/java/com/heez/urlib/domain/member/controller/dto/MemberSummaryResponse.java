package com.heez.urlib.domain.member.controller.dto;

import com.heez.urlib.domain.member.service.dto.MemberSummaryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 요약정보 응답 DTO")
public record MemberSummaryResponse(

    @Schema(description = "사용자 ID", example = "123456789000")
    Long memberId,

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.png")
    String memberImageUrl
) {

  public static MemberSummaryResponse from(MemberSummaryProjection projection) {
    return new MemberSummaryResponse(
        projection.getId(),
        projection.getImageUrl()
    );
  }

}
