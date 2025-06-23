package com.heez.urlib.domain.member.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.member.controller.dto.FollowStatusResponse;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import com.heez.urlib.domain.member.service.FollowService;
import com.heez.urlib.global.swagger.ApiErrorResponses_BadRequestOnly;
import com.heez.urlib.global.swagger.ApiErrorResponses_Unauthorized;
import com.heez.urlib.global.swagger.ApiErrorResponses_Unauthorized_Forbidden_Conflict;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "팔로우 API", description = "팔로우 관련 기능 제공")
@RequiredArgsConstructor
public class FollowController {

  private final FollowService followService;

  @Operation(
      summary = "사용자 팔로우 상태 조회",
      description = "로그인한 사용자가 다른 사용자에게 팔로우 상태를 조회합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "팔로우 상태 조회",
      content = @Content(schema = @Schema(implementation = FollowStatusResponse.class)))
  @GetMapping("/{memberId}/follow")
  public ResponseEntity<FollowStatusResponse> getFollowStatus(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "상태 조회할 사용자 ID", example = "42")
      @PathVariable Long memberId
  ) {
    return ResponseEntity.ok(followService.getFollowStatus(memberId, userPrincipal.getMemberId()));
  }

  @Operation(
      summary = "사용자 팔로우",
      description = "로그인한 사용자가 다른 사용자를 팔로우 합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "204",
      description = "팔로우 성공")
  @ApiErrorResponses_Unauthorized_Forbidden_Conflict
  @PostMapping("/{memberId}/follow")
  public ResponseEntity<Void> followOther(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "팔로우할 사용자 ID", example = "42")
      @PathVariable Long memberId
  ) {
    followService.follow(memberId, userPrincipal.getMemberId());
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "사용자 팔로우 해제",
      description = "로그인한 사용자가 다른 사용자를 팔로우 해제를 누릅니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "204",
      description = "좋아요 해제 성공")
  @ApiErrorResponses_Unauthorized_Forbidden_Conflict
  @DeleteMapping("/{memberId}/follow")
  public ResponseEntity<Void> unfollowOther(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "팔로우 해제할 사용자 ID", example = "42")
      @PathVariable Long memberId
  ) {
    followService.unfollow(memberId, userPrincipal.getMemberId());
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "팔로잉 목록 조회",
      description = "특정 사용자가 팔로우한 사용자 목록을 조회합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "사용자 팔로잉 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = MemberSummaryResponse.class))))
  @ApiErrorResponses_BadRequestOnly
  @GetMapping("/{memberId}/following")
  public ResponseEntity<Page<MemberSummaryResponse>> getFollowingList(
      @Parameter(description = "팔로잉 목록 조회할 사용자 ID", example = "42")
      @PathVariable Long memberId,

      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowingList(memberId, pageable));
  }

  @Operation(
      summary = "팔로워 목록 조회",
      description = "특정 사용자를 팔로우한 사용자 목록을 조회합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "사용자 팔로워 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = MemberSummaryResponse.class))))
  @ApiErrorResponses_BadRequestOnly
  @GetMapping("/{memberId}/follower")
  public ResponseEntity<Page<MemberSummaryResponse>> getFollowerList(
      @Parameter(description = "팔로워 목록 조회할 사용자 ID", example = "42")
      @PathVariable Long memberId,

      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowerList(memberId, pageable));
  }

  @Operation(
      summary = "내 팔로잉 목록 조회",
      description = "내가 팔로우한 사용자 목록을 조회합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "내 팔로잉 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = MemberSummaryResponse.class))))
  @ApiErrorResponses_Unauthorized
  @GetMapping("/me/following")
  public ResponseEntity<Page<MemberSummaryResponse>> getMyFollowingList(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowingList(userPrincipal.getMemberId(), pageable));
  }

  @Operation(
      summary = "내 팔로워 목록 조회",
      description = "나를 팔로우한 사용자 목록을 조회합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "내 팔로워 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = MemberSummaryResponse.class))))
  @ApiErrorResponses_Unauthorized
  @GetMapping("/me/follower")
  public ResponseEntity<Page<MemberSummaryResponse>> getMyFollowerList(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowerList(userPrincipal.getMemberId(), pageable));
  }

}
