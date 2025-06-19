package com.heez.urlib.domain.bookmark.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkShareRequest;
import com.heez.urlib.domain.bookmark.service.BookmarkPermissionService;
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
@RequiredArgsConstructor
public class BookmarkShareController {

  private final BookmarkPermissionService bookmarkPermissionService;

  @PutMapping("/{bookmarkId}/shares")
  public ResponseEntity<Void> updateBookmarkShare(
      @AuthUser(required = true) UserPrincipal userPrincipal,
      @PathVariable("bookmarkId") Long bookmarkId,
      @RequestBody @Valid BookmarkShareRequest request
  ) {
    bookmarkPermissionService.updateBookmarkShare(bookmarkId, userPrincipal.getMemberId(), request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{bookmarkId}/shares/{memberId}")
  public ResponseEntity<Void> deleteBookmarkShare(
      @AuthUser(required = true) UserPrincipal userPrincipal,
      @PathVariable("bookmarkId") Long bookmarkId,
      @PathVariable("memberId") Long memberId
  ) {
    bookmarkPermissionService.deleteBookmarkShare(bookmarkId, userPrincipal.getMemberId(),
        memberId);
    return ResponseEntity.noContent().build();
  }


  @DeleteMapping("/{bookmarkId}/shares/me")
  public ResponseEntity<Void> leaveBookmarkShare(
      @AuthUser(required = true) UserPrincipal userPrincipal,
      @PathVariable("bookmarkId") Long bookmarkId
  ) {
    bookmarkPermissionService.leaveBookmarkShare(bookmarkId, userPrincipal.getMemberId());
    return ResponseEntity.noContent().build();
  }
}
