package com.heez.urlib.domain.bookmark.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkSummaryResponse;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
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
@RequiredArgsConstructor
public class UserBookmarkController {

  private final BookmarkService bookmarkService;

  @GetMapping("/me/bookmarks")
  public ResponseEntity<Page<BookmarkSummaryResponse>> getMyBookmarks(
      @AuthUser(required = true) UserPrincipal userPrincipal,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        bookmarkService.getBookmarkSummaryListByMemberId(
            Optional.of(userPrincipal.getMemberId()), userPrincipal.getMemberId(), pageable));
  }


  @GetMapping("/{memberId}/bookmarks")
  public ResponseEntity<Page<BookmarkSummaryResponse>> getBookmarks(
      @AuthUser Optional<UserPrincipal> userPrincipal,
      @PathVariable("memberId") Long memberId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        bookmarkService.getBookmarkSummaryListByMemberId(
            userPrincipal.map(UserPrincipal::getMemberId), memberId, pageable));
  }
}
