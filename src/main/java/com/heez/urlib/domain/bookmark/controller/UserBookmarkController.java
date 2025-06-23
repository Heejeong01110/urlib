package com.heez.urlib.domain.bookmark.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkSummaryResponse;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.global.error.handler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "사용자 북마크 조회 API", description = "사용자 별 북마크 조회 기능")
@RequiredArgsConstructor
public class UserBookmarkController {

  private final BookmarkService bookmarkService;

  @Operation(
      summary = "내 북마크 목록 조회",
      description = "내 북마크 목록을 조회합니다.",
      security = @SecurityRequirement(name = "JWT")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "내 북마크 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = BookmarkDetailResponse.class))),
      @ApiResponse(responseCode = "400", description = "요청 형식 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "접근 권한 없음 (조회권한 없는 북마크 접근 시)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "북마크를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/me/bookmarks")
  public ResponseEntity<Page<BookmarkSummaryResponse>> getMyBookmarks(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        bookmarkService.getBookmarkSummaryListByMemberId(
            Optional.of(userPrincipal.getMemberId()), userPrincipal.getMemberId(), pageable));
  }

  @Operation(
      summary = "사용자 북마크 목록 조회",
      description = "사용자 북마크 목록을 조회합니다.",
      security = @SecurityRequirement(name = "JWT")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "사용자 북마크 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = BookmarkDetailResponse.class))),
      @ApiResponse(responseCode = "400", description = "요청 형식 오류 (잘못된 memberId 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "접근 권한 없음 (조회권한 없는 북마크 접근 시)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "북마크를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{memberId}/bookmarks")
  public ResponseEntity<Page<BookmarkSummaryResponse>> getBookmarks(
      @Parameter(hidden = true)
      @AuthUser Optional<UserPrincipal> userPrincipal,

      @Parameter(description = "북마크 목록 조회 할 사용자 ID", example = "10")
      @PathVariable("memberId") Long memberId,

      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        bookmarkService.getBookmarkSummaryListByMemberId(
            userPrincipal.map(UserPrincipal::getMemberId), memberId, pageable));
  }
}
