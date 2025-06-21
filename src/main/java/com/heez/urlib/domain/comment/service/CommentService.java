package com.heez.urlib.domain.comment.service;


import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.service.BookmarkPermissionService;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.domain.comment.controller.dto.CommentCreateRequest;
import com.heez.urlib.domain.comment.controller.dto.CommentDetailResponse;
import com.heez.urlib.domain.comment.model.Comment;
import com.heez.urlib.domain.comment.repository.CommentRepository;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.service.MemberService;
import jakarta.annotation.Nullable;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final CommentRepository commentRepository;
  private final MemberService memberService;
  private final BookmarkPermissionService bookmarkPermissionService;
  private final BookmarkService bookmarkService;

  public Page<CommentDetailResponse> getParentComments(
      Optional<Long> memberId, Long bookmarkId, Pageable pageable) {
    Bookmark bookmark = bookmarkService.findByBookmarkId(bookmarkId);
    bookmarkPermissionService.isVisible(bookmark, memberId);

    return commentRepository.findRootCommentsByBookmarkId(bookmarkId, pageable)
        .map(CommentDetailResponse::from);
  }

  @Transactional
  public CommentDetailResponse createComment(Long bookmarkId, Long memberId,
      CommentCreateRequest request) {
    Bookmark bookmark = bookmarkService.findByBookmarkId(bookmarkId);
    bookmarkPermissionService.isVisible(bookmark, Optional.of(memberId));
    Member member = memberService.findById(memberId);
    return CommentDetailResponse.from(
        saveComment(bookmark, member, request.content(), null), member);
  }

  @Transactional
  public Comment saveComment(
      Bookmark bookmark,
      Member member,
      String content,
      @Nullable Comment parentComment) {
    return commentRepository.save(Comment.builder()
        .bookmark(bookmark)
        .member(member)
        .content(content)
        .parentComment(parentComment)
        .build());
  }

}
