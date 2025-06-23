package com.heez.urlib.domain.member.controller.dto;

import com.heez.urlib.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 상세정보 응답 DTO")
public record MemberDetailResponse(

    @Schema(description = "사용자 ID", example = "123456789000")
    Long memberId,

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.png")
    String imageUrl,

    @Schema(description = "소개글", example = "소개글 입니다.")
    String description
) {

  public static MemberDetailResponse from(Member member) {
    return new MemberDetailResponse(
        member.getMemberId(),
        member.getImageUrl(),
        member.getDescription()
    );
  }
}
