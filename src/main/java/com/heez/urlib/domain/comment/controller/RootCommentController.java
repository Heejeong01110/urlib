package com.heez.urlib.domain.comment.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.comment.controller.dto.CommentCreateRequest;
import com.heez.urlib.domain.comment.controller.dto.CommentDetailResponse;
import com.heez.urlib.domain.comment.service.CommentService;
import com.heez.urlib.global.swagger.ApiErrorResponses_Forbidden;
import com.heez.urlib.global.swagger.ApiErrorResponses_Unauthorized_Forbidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookmarks/{bookmarkId}/comments")
@Tag(name = "북마크 댓글 API", description = "댓글 관련 기능 제공")
@RequiredArgsConstructor
public class RootCommentController {

  private final CommentService commentService;

  @Operation(
      summary = "댓글 목록 조회",
      description = "댓글 목록을 조회합니다. (로그인 시 공유받은 북마크와 본인 북마크의 댓글 목록 조회 가능합니다.)",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "댓글 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = CommentDetailResponse.class))))
  @ApiErrorResponses_Forbidden
  @GetMapping("")
  public ResponseEntity<Page<CommentDetailResponse>> getComments(
      @Parameter(hidden = true)
      @AuthUser Optional<UserPrincipal> userPrincipal,

      @Parameter(description = "댓글 목록 조회할 북마크 ID", example = "42")
      @PathVariable("bookmarkId") Long bookmarkId,

      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(commentService.getParentComments(
        userPrincipal.map(UserPrincipal::getMemberId), bookmarkId, pageable));
  }

  @Operation(
      summary = "댓글 생성",
      description = "로그인한 사용자가 댓글을 생성합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "201",
      description = "댓글 생성 성공",
      content = @Content(schema = @Schema(implementation = CommentDetailResponse.class)))
  @ApiErrorResponses_Unauthorized_Forbidden
  @PostMapping("")
  public ResponseEntity<CommentDetailResponse> createComment(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "댓글 목록 조회할 북마크 ID", example = "42")
      @PathVariable("bookmarkId") Long bookmarkId,

      @Valid @RequestBody CommentCreateRequest request
  ) {
    CommentDetailResponse response = commentService.createComment(bookmarkId,
        userPrincipal.getMemberId(), request);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.id())
        .toUri();

    return ResponseEntity.created(location).body(response);
  }
}
