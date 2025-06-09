package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.controller.dto.LikeResponse;
import com.heez.urlib.domain.bookmark.exception.AlreadyLikedException;
import com.heez.urlib.domain.bookmark.exception.AlreadyUnlikedException;
import com.heez.urlib.domain.bookmark.exception.BookmarkNotFoundException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.model.BookmarkLike;
import com.heez.urlib.domain.bookmark.repository.BookmarkLikeRepository;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.service.MemberService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkLikeService {

  private final BookmarkRepository bookmarkRepository;
  private final BookmarkLikeRepository bookmarkLikeRepository;
  private final MemberService memberService;
  private final BookmarkPermissionService bookmarkPermissionService;


  @Transactional
  public LikeResponse likeBookmark(Long memberId, Long bookmarkId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    bookmarkPermissionService.isVisible(bookmark, Optional.of(memberId));

    try {
      Member member = memberService.findById(memberId);
      BookmarkLike like = BookmarkLike.builder()
          .bookmark(bookmark)
          .member(member)
          .build();
      bookmarkLikeRepository.save(like);
      bookmarkRepository.incrementLikeCount(bookmarkId);
    } catch (DataIntegrityViolationException e) {
      throw new AlreadyLikedException();
    }

    return LikeResponse.builder()
        .liked(true)
        .likeCount(bookmark.getLikeCount())
        .build();
  }

  @Transactional
  public LikeResponse unlikeBookmark(Long memberId, Long bookmarkId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    bookmarkPermissionService.isVisible(bookmark, Optional.of(memberId));

    BookmarkLike bookmarkLike = bookmarkLikeRepository
        .findByBookmark_BookmarkIdAndMember_MemberId(bookmarkId, memberId)
        .orElseThrow(AlreadyUnlikedException::new);

    bookmarkLikeRepository.delete(bookmarkLike);
    bookmarkRepository.decrementLikeCount(bookmarkId);

    return LikeResponse.builder()
        .liked(false)
        .likeCount(bookmark.getLikeCount())
        .build();
  }
}
