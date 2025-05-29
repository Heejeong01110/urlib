package com.heez.urlib.domain.bookmark.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.heez.urlib.domain.bookmark.controller.dto.LikeResponse;
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

@ExtendWith(MockitoExtension.class)
class BookmarkLikeServiceImplTest {

  @Mock
  private BookmarkRepository bookmarkRepository;

  @Mock
  private BookmarkLikeRepository bookmarkLikeRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private BookmarkPermissionService bookmarkPermissionService;

  @InjectMocks
  private BookmarkLikeServiceImpl bookmarkLikeService;

  @Test
  void likeBookmark_notPreviouslyLiked_savesAndReturnsLikeResponse() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    Bookmark bookmark = mock(Bookmark.class);
    Member member = mock(Member.class);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    given(memberService.findById(memberId)).willReturn(member);
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, memberId);
    given(bookmarkLikeRepository
        .findByBookmark_BookmarkIdAndMember_Id(bookmarkId, memberId))
        .willReturn(Optional.empty());
    given(bookmark.getLikeCount()).willReturn(5L);

    // when
    LikeResponse response = bookmarkLikeService.likeBookmark(memberId, bookmarkId);

    // then
    assertThat(response.liked()).isTrue();
    assertThat(response.likeCount()).isEqualTo(5);

    then(bookmarkRepository).should().findById(bookmarkId);
    then(memberService).should().findById(memberId);
    then(bookmarkPermissionService).should().isVisible(bookmark, memberId);
    then(bookmarkLikeRepository).should()
        .findByBookmark_BookmarkIdAndMember_Id(bookmarkId, memberId);
    then(bookmarkLikeRepository).should().save(any(BookmarkLike.class));
    then(bookmark).should().incrementLikes();
  }

  @Test
  void likeBookmark_alreadyLiked_returnsLikeResponseWithoutSaving() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    Bookmark bookmark = mock(Bookmark.class);
    Member member = mock(Member.class);
    BookmarkLike existingLike = mock(BookmarkLike.class);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    given(memberService.findById(memberId)).willReturn(member);
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, memberId);
    given(bookmarkLikeRepository
        .findByBookmark_BookmarkIdAndMember_Id(bookmarkId, memberId))
        .willReturn(Optional.of(existingLike));
    given(bookmark.getLikeCount()).willReturn(3L);

    // when
    LikeResponse response = bookmarkLikeService.likeBookmark(memberId, bookmarkId);

    // then
    assertThat(response.liked()).isTrue();
    assertThat(response.likeCount()).isEqualTo(3);

    then(bookmarkLikeRepository).should(never()).save(any(BookmarkLike.class));
    then(bookmark).should(never()).incrementLikes();
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
    Bookmark bookmark = mock(Bookmark.class);
    Member member = mock(Member.class);
    BookmarkLike existingLike = mock(BookmarkLike.class);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    given(memberService.findById(memberId)).willReturn(member);
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, memberId);
    given(bookmarkLikeRepository
        .findByBookmark_BookmarkIdAndMember_Id(bookmarkId, memberId))
        .willReturn(Optional.of(existingLike));
    given(bookmark.getLikeCount()).willReturn(7L);

    // when
    LikeResponse response = bookmarkLikeService.unlikeBookmark(memberId, bookmarkId);

    // then
    assertThat(response.liked()).isTrue();
    assertThat(response.likeCount()).isEqualTo(7);

    then(bookmarkLikeRepository).should().delete(existingLike);
    then(bookmark).should().incrementLikes();
  }

  @Test
  void unlikeBookmark_notLiked_returnsLikeResponseWithoutDeleting() {
    // given
    Long memberId = 1L;
    Long bookmarkId = 2L;
    Bookmark bookmark = mock(Bookmark.class);
    Member member = mock(Member.class);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    given(memberService.findById(memberId)).willReturn(member);
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, memberId);
    given(bookmarkLikeRepository
        .findByBookmark_BookmarkIdAndMember_Id(bookmarkId, memberId))
        .willReturn(Optional.empty());
    given(bookmark.getLikeCount()).willReturn(2L);

    // when
    LikeResponse response = bookmarkLikeService.unlikeBookmark(memberId, bookmarkId);

    // then
    assertThat(response.liked()).isTrue();
    assertThat(response.likeCount()).isEqualTo(2);

    then(bookmarkLikeRepository).should(never()).delete(any(BookmarkLike.class));
    then(bookmark).should(never()).incrementLikes();
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
