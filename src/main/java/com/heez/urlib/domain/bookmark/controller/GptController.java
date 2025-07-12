package com.heez.urlib.domain.bookmark.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkTitleSuggestRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkTitleSuggestResponse;
import com.heez.urlib.domain.bookmark.service.GptService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "OPENAI API", description = "챗지피티 관련 기능 제공")
@RequiredArgsConstructor
public class GptController {

  private final GptService gptService;

  @PostMapping("/suggestions/bookmarks/title")
  public ResponseEntity<BookmarkTitleSuggestResponse> titleSuggest(
      @Parameter(hidden = true)
      @AuthUser(required = true) UserPrincipal userPrincipal,

      @Valid @RequestBody BookmarkTitleSuggestRequest request) {
    return ResponseEntity.ok(gptService.getSuggestion(request));
  }

}
