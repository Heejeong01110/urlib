package com.heez.urlib.domain.member.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.member.controller.dto.MemberDetailResponse;
import com.heez.urlib.domain.member.service.MemberService;
import com.heez.urlib.global.swagger.ApiErrorResponses_BadRequestOnly;
import com.heez.urlib.global.swagger.ApiErrorResponses_Unauthorized_Forbidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "사용자 프로필 API", description = "프로필 관련 기능 제공")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @Operation(
      summary = "내 프로필 조회",
      description = "내 프로필 정보를 조회합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "내 프로필 조회 성공",
      content = @Content(schema = @Schema(implementation = MemberDetailResponse.class)))
  @ApiErrorResponses_Unauthorized_Forbidden
  @GetMapping("/me")
  public ResponseEntity<MemberDetailResponse> getMyProfile(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal
  ) {
    return ResponseEntity.ok(memberService.getProfile(userPrincipal.getMemberId()));
  }

  @Operation(
      summary = "사용자 프로필 조회",
      description = "사용자 프로필 정보를 조회합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "사용자 프로필 조회 성공",
      content = @Content(schema = @Schema(implementation = MemberDetailResponse.class)))
  @ApiErrorResponses_BadRequestOnly
  @GetMapping("/{memberId}")
  public ResponseEntity<MemberDetailResponse> getProfile(
      @Parameter(description = "조회할 사용자 ID", example = "42")
      @PathVariable("memberId") Long memberId
  ) {
    return ResponseEntity.ok(memberService.getProfile(memberId));
  }
}
