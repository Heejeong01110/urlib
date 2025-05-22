package com.heez.urlib.domain.bookmark.controller;


import com.heez.urlib.domain.auth.model.CustomOAuth2User;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.domain.member.model.AuthUser;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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


  @PostMapping("/")
  public ResponseEntity<BookmarkCreateResponse> generateBookmark(
      @AuthUser CustomOAuth2User oauth2User,
      @Valid @RequestBody BookmarkCreateRequest request) {
    BookmarkCreateResponse response = bookmarkService.createBookmark(oauth2User.getMemberId(), request);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.id())
        .toUri();

    return ResponseEntity.created(location).body(response);
  }


}
