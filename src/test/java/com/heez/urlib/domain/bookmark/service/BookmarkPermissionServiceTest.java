package com.heez.urlib.domain.bookmark.service;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkShareRequest;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkModifyException;
import com.heez.urlib.domain.bookmark.exception.BookmarkNotFoundException;
import com.heez.urlib.domain.bookmark.exception.BookmarkShareNotFoundException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.model.BookmarkShare;
import com.heez.urlib.domain.bookmark.model.ShareRole;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.service.MemberService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookmarkPermissionServiceTest {

  @Mock
  private BookmarkRepository bookmarkRepository;

  @Mock
  private MemberService memberService;

  @InjectMocks
  private BookmarkPermissionService bookmarkPermissionService;

  @Test
  void isVisible_publicBookmark_doesNotThrowException() {
    // given
    Bookmark bookmark = Bookmark.builder()
        .visibleToOthers(true)
        .member(Member.builder().build())
        .bookmarkShares(new ArrayList<>())
        .build();

    // when & then
    assertThatCode(() ->
        bookmarkPermissionService.isVisible(bookmark, Optional.empty()))
        .doesNotThrowAnyException();
  }

  @Test
  void isVisible_ownerBookmark_doesNotThrowException() {
    // given
    Long viewerId = 1L;
    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", viewerId);

    Bookmark bookmark = Bookmark.builder()
        .visibleToOthers(false)
        .member(owner)
        .bookmarkShares(new ArrayList<>())
        .build();

    // when & then
    assertThatCode(() ->
        bookmarkPermissionService.isVisible(bookmark, Optional.of(viewerId)))
        .doesNotThrowAnyException();
  }

  @Test
  void isVisible_sharedViewer_doesNotThrowException() {
    // given
    Long viewerId = 2L;
    Member sharedMember = Member.builder().build();
    ReflectionTestUtils.setField(sharedMember, "memberId", viewerId);

    BookmarkShare share = BookmarkShare.builder()
        .member(sharedMember)
        .role(ShareRole.BOOKMARK_VIEWER)
        .build();

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", viewerId);

    Bookmark bookmark = Bookmark.builder()
        .visibleToOthers(false)
        .member(owner)
        .bookmarkShares(List.of(share))
        .build();

    // when & then
    assertThatCode(() -> bookmarkPermissionService.isVisible(bookmark, Optional.of(viewerId)))
        .doesNotThrowAnyException();
  }

  @Test
  void isVisible_sharedEditor_doesNotThrowException() {
    // given
    Long viewerId = 3L;
    Member sharedEditor = Member.builder().build();
    ReflectionTestUtils.setField(sharedEditor, "memberId", viewerId);

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 999L);

    BookmarkShare share = BookmarkShare.builder()
        .member(sharedEditor)
        .role(ShareRole.BOOKMARK_EDITOR)
        .build();

    Bookmark bookmark = Bookmark.builder()
        .visibleToOthers(false)
        .member(owner)
        .bookmarkShares(List.of(share))
        .build();

    // when & then
    assertThatCode(() ->
        bookmarkPermissionService.isVisible(bookmark, Optional.of(viewerId)))
        .doesNotThrowAnyException();
  }

  @Test
  void isVisible_notPublic_notShared_notOwner_throwsAccessDenied() {
    // given
    Long viewerId = 4L;
    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 999L);
    Bookmark bookmark = Bookmark.builder()
        .visibleToOthers(false)
        .member(owner)
        .bookmarkShares(new ArrayList<>())
        .build();

    // when & then
    assertThatThrownBy(() -> bookmarkPermissionService.isVisible(bookmark, Optional.of(viewerId)))
        .isInstanceOf(AccessDeniedBookmarkException.class);
  }

  @Test
  void isVisible_anonymousUser_notPublic_throwsAccessDenied() {
    // given
    Bookmark bookmark = Bookmark.builder()
        .visibleToOthers(false)
        .member(Member.builder().build())
        .bookmarkShares(new ArrayList<>())
        .build();

    // when & then
    assertThatThrownBy(() -> bookmarkPermissionService.isVisible(bookmark, Optional.empty()))
        .isInstanceOf(AccessDeniedBookmarkException.class);
  }

  @Test
  void isEditable_ownerBookmark_doesNotThrowException() {
    // given
    Long viewerId = 1L;
    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", viewerId);

    Bookmark bookmark = Bookmark.builder()
        .member(owner)
        .bookmarkShares(new ArrayList<>())
        .build();

    // when & then
    assertThatCode(() -> bookmarkPermissionService.isEditable(bookmark, viewerId))
        .doesNotThrowAnyException();
  }

  @Test
  void isEditable_sharedEditorBookmark_doesNotThrowException() {
    // given
    Long viewerId = 2L;
    Member sharedEditor = Member.builder().build();
    ReflectionTestUtils.setField(sharedEditor, "memberId", viewerId);

    BookmarkShare shared = BookmarkShare.builder()
        .member(sharedEditor)
        .role(ShareRole.BOOKMARK_EDITOR)
        .build();

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 999L);

    Bookmark bookmark = Bookmark.builder()
        .member(owner)
        .bookmarkShares(List.of(shared))
        .build();

    // when & then
    assertThatCode(() -> bookmarkPermissionService.isEditable(bookmark, viewerId))
        .doesNotThrowAnyException();
  }

  @Test
  void isEditable_sharedViewerBookmark_throwsAccessDeniedException() {
    // given
    Long viewerId = 3L;
    Member sharedViewer = Member.builder().build();
    ReflectionTestUtils.setField(sharedViewer, "memberId", viewerId);

    BookmarkShare shared = BookmarkShare.builder()
        .member(sharedViewer)
        .role(ShareRole.BOOKMARK_VIEWER)  // 읽기 권한만 있음
        .build();

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 999L);

    Bookmark bookmark = Bookmark.builder()
        .member(owner) // 다른 사람이 주인
        .bookmarkShares(List.of(shared))
        .build();

    // when & then
    assertThatThrownBy(() -> bookmarkPermissionService.isEditable(bookmark, viewerId))
        .isInstanceOf(AccessDeniedBookmarkModifyException.class);
  }

  @Test
  void isEditable_unsharedBookmark_throwsAccessDeniedException() {
    // given
    Long viewerId = 4L;
    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 999L);
    Bookmark bookmark = Bookmark.builder()
        .member(owner)
        .bookmarkShares(new ArrayList<>()) // 공유 안됨
        .build();

    // when & then
    assertThatThrownBy(() -> bookmarkPermissionService.isEditable(bookmark, viewerId))
        .isInstanceOf(AccessDeniedBookmarkModifyException.class);
  }

  @Test
  void isOwner_correctOwner_doesNotThrowException() {
    // given
    Long viewerId = 1L;
    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", viewerId);

    Bookmark bookmark = Bookmark.builder()
        .member(owner)
        .build();

    // when & then
    assertThatCode(() -> bookmarkPermissionService.isOwner(bookmark, viewerId))
        .doesNotThrowAnyException();
  }

  @Test
  void isOwner_notOwner_throwsAccessDeniedException() {
    // given
    Long viewerId = 2L;
    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 999L); // 다른 ID

    Bookmark bookmark = Bookmark.builder()
        .member(owner)
        .build();

    // when & then
    assertThatThrownBy(() -> bookmarkPermissionService.isOwner(bookmark, viewerId))
        .isInstanceOf(AccessDeniedBookmarkModifyException.class);
  }

  @Test
  void updateBookmarkShare_validEditorRequest_updatesSuccessfully() {
    // given
    Long bookmarkId = 1L;
    Long ownerId = 10L;
    Long targetMemberId = 20L;

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", ownerId);

    Member target = Member.builder().build();
    ReflectionTestUtils.setField(target, "memberId", targetMemberId);

    Bookmark bookmark = Bookmark.builder()
        .member(owner)
        .bookmarkShares(new ArrayList<>())
        .build();

    BookmarkShareRequest request = new BookmarkShareRequest(targetMemberId,
        ShareRole.BOOKMARK_VIEWER);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    given(memberService.findById(targetMemberId)).willReturn(target);

    // when
    bookmarkPermissionService.updateBookmarkShare(bookmarkId, ownerId, request);

    // then
    then(bookmarkRepository).should().findById(bookmarkId);
    then(memberService).should().findById(targetMemberId);

    assertThat(bookmark.getBookmarkShares()).hasSize(1);
    BookmarkShare result = bookmark.getBookmarkShares().get(0);
    assertThat(result.getMember().getMemberId()).isEqualTo(targetMemberId);
    assertThat(result.getRole()).isEqualTo(ShareRole.BOOKMARK_VIEWER);
  }

  @Test
  void updateBookmarkShare_noPermission_throwsAccessDenied() {
    // given
    Long bookmarkId = 1L;
    Long memberId = 999L;

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 10L);
    Bookmark bookmark = Bookmark.builder().member(owner).build();
    BookmarkShareRequest request = new BookmarkShareRequest(10L, ShareRole.BOOKMARK_VIEWER);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));

    // when & then
    assertThatThrownBy(
        () -> bookmarkPermissionService.updateBookmarkShare(bookmarkId, memberId, request))
        .isInstanceOf(AccessDeniedBookmarkModifyException.class);

    then(bookmarkRepository).should().findById(bookmarkId);
    then(memberService).should(never()).findById(any());
  }

  @Test
  void updateBookmarkShare_bookmarkNotFound_throwsException() {
    // given
    Long bookmarkId = 1L;
    Long memberId = 2L;
    BookmarkShareRequest request = new BookmarkShareRequest(3L, ShareRole.BOOKMARK_EDITOR);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(
        () -> bookmarkPermissionService.updateBookmarkShare(bookmarkId, memberId, request))
        .isInstanceOf(BookmarkNotFoundException.class);

    then(bookmarkRepository).should().findById(bookmarkId);
    then(memberService).shouldHaveNoInteractions();
  }

  @Test
  void deleteBookmarkShare_validRequest_removesShare() {
    // given
    Long bookmarkId = 1L;
    Long ownerId = 10L;
    Long targetId = 20L;

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", ownerId);

    Member target = Member.builder().build();
    ReflectionTestUtils.setField(target, "memberId", targetId);

    BookmarkShare shareToRemove = BookmarkShare.builder()
        .member(target)
        .role(ShareRole.BOOKMARK_VIEWER)
        .build();

    List<BookmarkShare> shares = new ArrayList<>();
    shares.add(shareToRemove);

    Bookmark bookmark = Bookmark.builder()
        .member(owner)
        .bookmarkShares(shares)
        .build();

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));

    // when
    bookmarkPermissionService.deleteBookmarkShare(bookmarkId, ownerId, targetId);

    // then
    then(bookmarkRepository).should().findById(bookmarkId);
    assertThat(bookmark.getBookmarkShares()).isEmpty(); // 공유자 제거 확인
  }

  @Test
  void deleteBookmarkShare_noPermission_throwsAccessDenied() {
    // given
    Long bookmarkId = 1L;
    Long ownerId = 10L;
    Long targetId = 20L;

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 999L);
    Bookmark bookmark = Bookmark.builder().member(owner).build();

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));

    // when & then
    assertThatThrownBy(
        () -> bookmarkPermissionService.deleteBookmarkShare(bookmarkId, ownerId, targetId))
        .isInstanceOf(AccessDeniedBookmarkModifyException.class);

    then(bookmarkRepository).should().findById(bookmarkId);
  }

  @Test
  void deleteBookmarkShare_bookmarkNotFound_throwsException() {
    // given
    Long bookmarkId = 1L;
    Long ownerId = 10L;
    Long targetId = 20L;

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(
        () -> bookmarkPermissionService.deleteBookmarkShare(bookmarkId, ownerId, targetId))
        .isInstanceOf(BookmarkNotFoundException.class);

    then(bookmarkRepository).should().findById(bookmarkId);
  }

  @Test
  void leaveBookmarkShare_sharedMember_removesSuccessfully() {
    // given
    Long bookmarkId = 1L;
    Long sharedMemberId = 10L;

    Member sharedMember = Member.builder().build();
    ReflectionTestUtils.setField(sharedMember, "memberId", sharedMemberId);

    BookmarkShare share = BookmarkShare.builder()
        .member(sharedMember)
        .role(ShareRole.BOOKMARK_VIEWER)
        .build();

    Bookmark bookmark = Bookmark.builder()
        .member(Member.builder().build())
        .bookmarkShares(new ArrayList<>(List.of(share)))
        .build();

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));

    // when
    bookmarkPermissionService.leaveBookmarkShare(bookmarkId, sharedMemberId);

    // then
    then(bookmarkRepository).should().findById(bookmarkId);
    assertThat(bookmark.getBookmarkShares()).isEmpty();
  }

  @Test
  void leaveBookmarkShare_notInSharedList_throwsBookmarkShareNotFoundException() {
    // given
    Long bookmarkId = 1L;
    Long memberId = 999L;

    Bookmark bookmark = Bookmark.builder()
        .member(Member.builder().build())
        .bookmarkShares(new ArrayList<>()) // 공유자 없음
        .build();

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));

    // when & then
    assertThatThrownBy(() ->
        bookmarkPermissionService.leaveBookmarkShare(bookmarkId, memberId)
    ).isInstanceOf(BookmarkShareNotFoundException.class);

    then(bookmarkRepository).should().findById(bookmarkId);
  }

  @Test
  void leaveBookmarkShare_bookmarkNotFound_throwsBookmarkNotFoundException() {
    // given
    Long bookmarkId = 1L;
    Long memberId = 10L;

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        bookmarkPermissionService.leaveBookmarkShare(bookmarkId, memberId)
    ).isInstanceOf(BookmarkNotFoundException.class);

    then(bookmarkRepository).should().findById(bookmarkId);
  }
}
