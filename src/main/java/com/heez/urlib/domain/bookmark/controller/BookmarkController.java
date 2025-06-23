package com.heez.urlib.domain.bookmark.controller;


import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkSummaryResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkUpdateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.LikeResponse;
import com.heez.urlib.domain.bookmark.service.BookmarkLikeService;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.global.swagger.ApiErrorResponses_BadRequestOnly;
import com.heez.urlib.global.swagger.ApiErrorResponses_Forbidden;
import com.heez.urlib.global.swagger.ApiErrorResponses_Unauthorized;
import com.heez.urlib.global.swagger.ApiErrorResponses_Unauthorized_Forbidden;
import com.heez.urlib.global.swagger.ApiErrorResponses_Unauthorized_Forbidden_Conflict;
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
@RequestMapping("/api/v1/bookmarks")
@Tag(name = "북마크 API", description = "북마크 관련 기능 제공")
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;
  private final BookmarkLikeService bookmarkLikeService;

  @Operation(
      summary = "북마크 목록 조회",
      description = "전체 공개 북마크 목록을 조회합니다. (로그인 시 공유받은 북마크와 본인 북마크도 함께 조회됩니다.)",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "북마크 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = BookmarkSummaryResponse.class))))
  @ApiErrorResponses_BadRequestOnly
  @GetMapping("")
  public ResponseEntity<Page<BookmarkSummaryResponse>> getBookmarks(
      @Parameter(hidden = true)
      @AuthUser Optional<UserPrincipal> userPrincipal,

      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    return ResponseEntity.ok(
        bookmarkService.getBookmarkSummaryList(
            userPrincipal.map(UserPrincipal::getMemberId), pageable));
  }

  @Operation(
      summary = "북마크 생성",
      description = "로그인한 사용자가 새 북마크를 생성합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "201",
      description = "북마크 생성 성공",
      content = @Content(schema = @Schema(implementation = BookmarkCreateResponse.class)))
  @ApiErrorResponses_Unauthorized
  @PostMapping("")
  public ResponseEntity<BookmarkCreateResponse> generateBookmark(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Valid @RequestBody BookmarkCreateRequest request) {
    BookmarkCreateResponse response =
        bookmarkService.createBookmark(userPrincipal.getMemberId(), request);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.id())
        .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @Operation(
      summary = "북마크 상세정보 조회",
      description = "북마크 상세정보를 조회합니다. (로그인 시 본인이 조회 가능한 북마크인 경우 포함 조회합니다.)",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "북마크 조회 성공",
      content = @Content(schema = @Schema(implementation = BookmarkDetailResponse.class)))
  @ApiErrorResponses_Forbidden
  @GetMapping("/{bookmarkId}")
  public ResponseEntity<BookmarkDetailResponse> getBookmark(
      @Parameter(hidden = true)
      @AuthUser Optional<UserPrincipal> userPrincipal,

      @Parameter(description = "조회할 북마크 ID", example = "42")
      @PathVariable("bookmarkId") Long bookmarkId
  ) {
    return ResponseEntity.ok(
        bookmarkService.getBookmark(userPrincipal.map(UserPrincipal::getMemberId), bookmarkId));
  }

  @Operation(
      summary = "북마크 수정",
      description = "북마크 정보를 수정합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "북마크 수정 성공",
      content = @Content(schema = @Schema(implementation = BookmarkDetailResponse.class)))
  @ApiErrorResponses_Unauthorized_Forbidden
  @PutMapping("/{bookmarkId}")
  public ResponseEntity<BookmarkDetailResponse> updateBookmark(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "수정할 북마크 ID", example = "42")
      @PathVariable Long bookmarkId,

      @RequestBody @Valid BookmarkUpdateRequest request
  ) {
    return ResponseEntity.ok(bookmarkService.updateBookmark(
        userPrincipal.getMemberId(), bookmarkId, request));
  }

  @Operation(
      summary = "북마크 삭제",
      description = "북마크 정보를 삭제합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "204",
      description = "북마크 삭제 성공")
  @ApiErrorResponses_Unauthorized_Forbidden
  @DeleteMapping("/{bookmarkId}")
  public ResponseEntity<Void> deleteBookmark(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "삭제할 북마크 ID", example = "42")
      @PathVariable Long bookmarkId
  ) {
    bookmarkService.deleteBookmark(userPrincipal.getMemberId(), bookmarkId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "북마크 좋아요",
      description = "로그인한 사용자가 북마크에 좋아요를 누릅니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "좋아요 성공",
      content = @Content(schema = @Schema(implementation = LikeResponse.class)))
  @ApiErrorResponses_Unauthorized_Forbidden_Conflict
  @PostMapping("/{bookmarkId}/like")
  public ResponseEntity<LikeResponse> like(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "좋아요 누를 북마크 ID", example = "42")
      @PathVariable Long bookmarkId
  ) {
    return ResponseEntity.ok(
        bookmarkLikeService.likeBookmark(userPrincipal.getMemberId(), bookmarkId));
  }

  @Operation(
      summary = "북마크 좋아요 해제",
      description = "로그인한 사용자가 북마크에 좋아요 해제를 누릅니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(
      responseCode = "200",
      description = "좋아요 해제 성공",
      content = @Content(schema = @Schema(implementation = LikeResponse.class)))
  @ApiErrorResponses_Unauthorized_Forbidden_Conflict
  @DeleteMapping("/{bookmarkId}/like")
  public ResponseEntity<LikeResponse> unlike(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "좋아요 해제할 북마크 ID", example = "42")
      @PathVariable Long bookmarkId
  ) {
    return ResponseEntity.ok(
        bookmarkLikeService.unlikeBookmark(userPrincipal.getMemberId(), bookmarkId));
  }

}
