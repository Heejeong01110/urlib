package com.heez.urlib.domain.bookmark.controller;


import com.heez.urlib.domain.auth.model.UserPrincipal;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkSummaryResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkUpdateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.LikeResponse;
import com.heez.urlib.domain.bookmark.service.BookmarkLikeService;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.domain.member.model.AuthUser;
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
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;
  private final BookmarkLikeService bookmarkLikeService;

  @GetMapping("")
  public ResponseEntity<Page<BookmarkSummaryResponse>> getBookmarks(
      @AuthUser Optional<UserPrincipal> userPrincipal,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        bookmarkService.getBookmarkSummaryList(
            userPrincipal.map(UserPrincipal::getMemberId), pageable));
  }

  @PostMapping("")
  public ResponseEntity<BookmarkCreateResponse> generateBookmark(
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

  @GetMapping("/{bookmarkId}")
  public ResponseEntity<BookmarkDetailResponse> getBookmark(
      @AuthUser Optional<UserPrincipal> userPrincipal,
      @PathVariable("bookmarkId") Long bookmarkId
  ) {
    return ResponseEntity.ok(
        bookmarkService.getBookmark(userPrincipal.map(UserPrincipal::getMemberId), bookmarkId));
  }

  @PutMapping("/{bookmarkId}")
  public ResponseEntity<BookmarkDetailResponse> updateBookmark(
      @AuthUser(required = true) UserPrincipal userPrincipal,
      @PathVariable Long bookmarkId,
      @RequestBody @Valid BookmarkUpdateRequest request
  ) {
    return ResponseEntity.ok(bookmarkService.updateBookmark(
        userPrincipal.getMemberId(), bookmarkId, request));
  }

  @DeleteMapping("/{bookmarkId}")
  public ResponseEntity<Void> deleteBookmark(
      @AuthUser(required = true) UserPrincipal userPrincipal,
      @PathVariable Long bookmarkId
  ) {
    bookmarkService.deleteBookmark(userPrincipal.getMemberId(), bookmarkId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{bookmarkId}/like")
  public ResponseEntity<LikeResponse> like(
      @AuthUser(required = true) UserPrincipal userPrincipal,
      @PathVariable Long bookmarkId
  ) {
    return ResponseEntity.ok(
        bookmarkLikeService.likeBookmark(userPrincipal.getMemberId(), bookmarkId));
  }

  @DeleteMapping("/{bookmarkId}/like")
  public ResponseEntity<LikeResponse> unlike(
      @AuthUser(required = true) UserPrincipal userPrincipal,
      @PathVariable Long bookmarkId
  ) {
    return ResponseEntity.ok(
        bookmarkLikeService.unlikeBookmark(userPrincipal.getMemberId(), bookmarkId));
  }

}
