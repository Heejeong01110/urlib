package com.heez.urlib.domain.comment.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.comment.controller.dto.CommentCreateRequest;
import com.heez.urlib.domain.comment.controller.dto.CommentDetailResponse;
import com.heez.urlib.domain.comment.controller.dto.CommentUpdateRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "북마크 댓글 API", description = "댓글 관련 기능 제공")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @Operation(
      summary = "대댓글 목록 조회",
      description = "대댓글 목록을 조회합니다. (로그인 시 공유받은 북마크와 본인 북마크의 댓글 목록 조회 가능합니다.)",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "대댓글 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = CommentDetailResponse.class))))
  @ApiErrorResponses_Forbidden
  @GetMapping("/{commentId}/replies")
  public ResponseEntity<Page<CommentDetailResponse>> getChildrenComments(
      @Parameter(hidden = true)
      @AuthUser Optional<UserPrincipal> userPrincipal,

      @Parameter(description = "대댓글 목록 조회할 댓글 ID", example = "42")
      @PathVariable("commentId") Long commentId,

      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(commentService.getChildrenComments(
        userPrincipal.map(UserPrincipal::getMemberId), commentId, pageable));
  }

  @Operation(
      summary = "대댓글 생성",
      description = "로그인한 사용자가 대댓글을 생성합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "201",
      description = "대댓글 생성 성공",
      content = @Content(schema = @Schema(implementation = CommentDetailResponse.class)))
  @ApiErrorResponses_Unauthorized_Forbidden
  @PostMapping("/{commentId}/replies")
  public ResponseEntity<CommentDetailResponse> createChildrenComment(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "대댓글 목록 조회할 댓글 ID", example = "42")
      @PathVariable("commentId") Long commentId,

      @Valid @RequestBody CommentCreateRequest request
  ) {
    CommentDetailResponse response = commentService.createReplyComment(commentId,
        userPrincipal.getMemberId(), request);

    URI location = ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/api/v1/comments/{id}")
        .buildAndExpand(response.id())
        .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @Operation(
      summary = "댓글 수정",
      description = "댓글 내용을 수정합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "댓글 수정 성공",
      content = @Content(schema = @Schema(implementation = BookmarkDetailResponse.class)))
  @ApiErrorResponses_Unauthorized_Forbidden
  @PutMapping("/{commentId}")
  public ResponseEntity<CommentDetailResponse> updateComment(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "수정할 댓글 ID", example = "42")
      @PathVariable("commentId") Long commentId,

      @RequestBody @Valid CommentUpdateRequest request
  ) {
    return ResponseEntity.ok(
        commentService.updateComment(userPrincipal.getMemberId(), commentId, request));
  }

  @Operation(
      summary = "댓글 삭제",
      description = "댓글 내용을 삭제합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "204",
      description = "댓글 삭제 성공")
  @ApiErrorResponses_Unauthorized_Forbidden
  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> deleteComment(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "삭제할 댓글 ID", example = "42")
      @PathVariable("commentId") Long commentId
  ) {
    commentService.deleteComment(userPrincipal.getMemberId(), commentId);
    return ResponseEntity.noContent().build();
  }

}
