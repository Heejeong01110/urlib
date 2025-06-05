package com.heez.urlib.domain.bookmark.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class BookmarkLikeServiceTest {

  @Mock
  private BookmarkRepository bookmarkRepository;

  @Mock
  private BookmarkLikeRepository bookmarkLikeRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private BookmarkPermissionService bookmarkPermissionService;

  @InjectMocks
  private BookmarkLikeService bookmarkLikeService;

  @Test
  void likeBookmark_notPreviouslyLiked_savesAndReturnsLikeResponse() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    Bookmark bookmark = Bookmark.builder()
        .likeCount(5L)
        .build();
    Member member = mock(Member.class);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(memberService.findById(memberId)).willReturn(member);

    // when
    LikeResponse response = bookmarkLikeService.likeBookmark(memberId, bookmarkId);

    // then
    assertThat(response.liked()).isTrue();
    assertThat(response.likeCount()).isEqualTo(6);

    then(bookmarkRepository).should().findById(bookmarkId);
    then(memberService).should().findById(memberId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
    then(bookmarkLikeRepository).should().save(any(BookmarkLike.class));
  }

  @Test
  void likeBookmark_alreadyLiked_throwsAlreadyLikedException() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    Bookmark bookmark = Bookmark.builder()
        .likeCount(5L)
        .visibleToOthers(true)
        .build();
    Member member = mock(Member.class);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(memberService.findById(memberId)).willReturn(member);
    willThrow(new DataIntegrityViolationException("duplicate key"))
        .given(bookmarkLikeRepository).save(any(BookmarkLike.class));

    // when / then
    assertThatThrownBy(() -> bookmarkLikeService.likeBookmark(memberId, bookmarkId))
        .isInstanceOf(AlreadyLikedException.class);

    then(bookmarkLikeRepository).should().save(any(BookmarkLike.class));
  }

  @Test
  void likeBookmark_bookmarkNotFound_throwsBookmarkNotFoundException() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    given(bookmarkRepository.findById(bookmarkId))
        .willReturn(Optional.empty());

    // when + then
    assertThatThrownBy(() -> bookmarkLikeService.likeBookmark(memberId, bookmarkId)
    ).isInstanceOf(BookmarkNotFoundException.class);
  }

  @Test
  void unlikeBookmark_previouslyLiked_deletesAndReturnsLikeResponse() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    Bookmark bookmark = Bookmark.builder()
        .likeCount(5L)
        .build();
    BookmarkLike existingLike = mock(BookmarkLike.class);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(bookmarkLikeRepository
        .findByBookmark_BookmarkIdAndMember_MemberId(bookmarkId, memberId))
        .willReturn(Optional.of(existingLike));

    // when
    LikeResponse response = bookmarkLikeService.unlikeBookmark(memberId, bookmarkId);

    // then
    assertThat(response.liked()).isFalse();
    assertThat(response.likeCount()).isEqualTo(4);

    then(bookmarkLikeRepository).should().delete(existingLike);
  }

  @Test
  void unlikeBookmark_notLiked_throwsAlreadyUnlikedException() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    Bookmark bookmark = mock(Bookmark.class);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    willThrow(new AlreadyUnlikedException())
        .given(bookmarkLikeRepository).findByBookmark_BookmarkIdAndMember_MemberId(bookmarkId, memberId);

    // when / then
    assertThatThrownBy(() -> bookmarkLikeService.unlikeBookmark(memberId, bookmarkId))
        .isInstanceOf(AlreadyUnlikedException.class);

  }

  @Test
  void unlikeBookmark_bookmarkNotFound_throwsBookmarkNotFoundException() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    given(bookmarkRepository.findById(bookmarkId))
        .willReturn(Optional.empty());

    // when + then
    assertThatThrownBy(() -> bookmarkLikeService.unlikeBookmark(memberId, bookmarkId)
    ).isInstanceOf(BookmarkNotFoundException.class);
  }
}
