package com.heez.urlib.domain.comment.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.comment.controller.dto.CommentCreateRequest;
import com.heez.urlib.domain.comment.controller.dto.CommentDetailResponse;
import com.heez.urlib.domain.comment.service.CommentService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/{commentId}/replies")
  public ResponseEntity<Page<CommentDetailResponse>> getChildrenComments(
      @AuthUser Optional<UserPrincipal> userPrincipal,
      @PathVariable("commentId") Long commentId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(commentService.getChildrenComments(
        userPrincipal.map(UserPrincipal::getMemberId), commentId, pageable));
  }


  @PostMapping("/{commentId}/replies")
  public ResponseEntity<CommentDetailResponse> createChildrenComment(
      @AuthUser(required = true) UserPrincipal userPrincipal,
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
  
}
