package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.controller.dto.LikeResponse;
import com.heez.urlib.domain.bookmark.exception.BookmarkNotFoundException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.model.BookmarkLike;
import com.heez.urlib.domain.bookmark.repository.BookmarkLikeRepository;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.service.MemberService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkLikeServiceImpl implements BookmarkLikeService {

  private final BookmarkRepository bookmarkRepository;
  private final BookmarkLikeRepository bookmarkLikeRepository;
  private final MemberService memberService;
  private final BookmarkPermissionService bookmarkPermissionService;


  @Override
  public LikeResponse likeBookmark(Long memberId, Long bookmarkId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    bookmarkPermissionService.isVisible(bookmark, memberId);
    Optional<BookmarkLike> bookmarkLike = bookmarkLikeRepository
        .findByBookmark_BookmarkIdAndMember_Id(bookmarkId, memberId);

    if (!bookmarkLike.isPresent()) {
      Member member = memberService.findById(memberId);
      BookmarkLike like = BookmarkLike.builder()
          .bookmark(bookmark)
          .member(member)
          .build();
      bookmarkLikeRepository.save(like);
      bookmark.incrementLikes();
    }

    return LikeResponse.builder()
        .liked(true)
        .likeCount(bookmark.getLikeCount())
        .build();
  }

  @Override
  public LikeResponse unlikeBookmark(Long memberId, Long bookmarkId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    Member member = memberService.findById(memberId);
    bookmarkPermissionService.isVisible(bookmark, memberId);
    Optional<BookmarkLike> bookmarkLike = bookmarkLikeRepository
        .findByBookmark_BookmarkIdAndMember_Id(bookmarkId, memberId);

    if (bookmarkLike.isPresent()) {
      bookmarkLikeRepository.delete(bookmarkLike.get());
      bookmark.incrementLikes();
    }

    return LikeResponse.builder()
        .liked(true)
        .likeCount(bookmark.getLikeCount())
        .build();
  }
}
