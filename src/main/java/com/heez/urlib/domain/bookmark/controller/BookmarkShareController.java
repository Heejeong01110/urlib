package com.heez.urlib.domain.bookmark.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkShareRequest;
import com.heez.urlib.domain.bookmark.service.BookmarkPermissionService;
import com.heez.urlib.global.error.handler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookmarks")
@Tag(name = "북마크 공유 API", description = "북마크 공유 설정 기능")
@RequiredArgsConstructor
public class BookmarkShareController {

  private final BookmarkPermissionService bookmarkPermissionService;

  @Operation(
      summary = "북마크 공유정보 수정",
      description = "북마크 공유정보를 수정합니다.",
      security = @SecurityRequirement(name = "JWT")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "북마크 공유정보 수정 성공"),
      @ApiResponse(responseCode = "400", description = "요청 형식 오류 (잘못된 bookmarkId 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "접근 권한 없음 (소유중이지 않은 북마크 접근 시)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "북마크를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/{bookmarkId}/shares")
  public ResponseEntity<Void> updateBookmarkShare(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "공유 설정할 북마크 ID", example = "42")
      @PathVariable("bookmarkId") Long bookmarkId,

      @RequestBody @Valid BookmarkShareRequest request
  ) {
    bookmarkPermissionService.updateBookmarkShare(bookmarkId, userPrincipal.getMemberId(), request);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "북마크 공유정보 삭제",
      description = "북마크 공유정보를 삭제합니다.",
      security = @SecurityRequirement(name = "JWT")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "북마크 공유정보 삭제 성공"),
      @ApiResponse(responseCode = "400", description = "요청 형식 오류 (잘못된 bookmarkId 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "접근 권한 없음 (소유중이지 않은 북마크 접근 시)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "북마크를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{bookmarkId}/shares/{memberId}")
  public ResponseEntity<Void> deleteBookmarkShare(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "공유 삭제할 북마크 ID", example = "42")
      @PathVariable("bookmarkId") Long bookmarkId,

      @Parameter(description = "공유 삭제 할 사용자 ID", example = "10")
      @PathVariable("memberId") Long memberId
  ) {
    bookmarkPermissionService.deleteBookmarkShare(bookmarkId, userPrincipal.getMemberId(),
        memberId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "북마크 나의 공유정보 삭제",
      description = "북마크 나의 공유정보를 삭제합니다.",
      security = @SecurityRequirement(name = "JWT")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "북마크 나의 공유정보 삭제 성공"),
      @ApiResponse(responseCode = "400", description = "요청 형식 오류 (잘못된 bookmarkId 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "접근 권한 없음 (소유중이지 않은 북마크 접근 시)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "북마크를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{bookmarkId}/shares/me")
  public ResponseEntity<Void> leaveBookmarkShare(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "공유 삭제할 북마크 ID", example = "42")
      @PathVariable("bookmarkId") Long bookmarkId
  ) {
    bookmarkPermissionService.leaveBookmarkShare(bookmarkId, userPrincipal.getMemberId());
    return ResponseEntity.noContent().build();
  }
}
