package com.heez.urlib.domain.bookmark.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkShareRequest;
import com.heez.urlib.domain.bookmark.service.BookmarkPermissionService;
import com.heez.urlib.global.swagger.ApiErrorResponses_Unauthorized_Forbidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(responseCode = "204", description = "북마크 공유정보 수정 성공")
  @ApiErrorResponses_Unauthorized_Forbidden
  @PutMapping("/{bookmarkId}/shares")
  public ResponseEntity<Void> updateBookmarkShare(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Parameter(description = "공유 설정할 북마크 ID", example = "42")
      @PathVariable("bookmarkId") Long bookmarkId,

      @RequestBody @Valid BookmarkShareRequest request
  ) {
    bookmarkPermissionService.updateBookmarkShare(bookmarkId, userPrincipal.getMemberId(), request);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "북마크 공유정보 삭제",
      description = "북마크 공유정보를 삭제합니다.",
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(responseCode = "204", description = "북마크 공유정보 삭제 성공")
  @ApiErrorResponses_Unauthorized_Forbidden
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
      security = @SecurityRequirement(name = "JWT"))
  @ApiResponse(responseCode = "204", description = "북마크 나의 공유정보 삭제 성공")
  @ApiErrorResponses_Unauthorized_Forbidden
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
