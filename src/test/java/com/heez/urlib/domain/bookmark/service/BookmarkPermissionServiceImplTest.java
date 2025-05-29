package com.heez.urlib.domain.bookmark.service;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkModifyException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.member.model.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookmarkPermissionServiceImplTest {

  @Mock
  private BookmarkRepository bookmarkRepository;

  @InjectMocks
  private BookmarkPermissionServiceImpl BookmarkPermissionService;

  @Test
  void isVisible_viewerIsOwner() {
    // given
    Bookmark bookmark = mock(Bookmark.class);
    when(bookmark.getBookmarkId()).thenReturn(1L);
    when(bookmarkRepository.existsByBookmarkIdAndMember_Id(1L, 42L)).thenReturn(true);

    // when + then
    assertThatThrownBy(() -> BookmarkPermissionService.isVisible(bookmark, 42L))
        .isInstanceOf(AccessDeniedBookmarkException.class);

    verify(bookmarkRepository).existsByBookmarkIdAndMember_Id(1L, 42L);
  }

  @Test
  void isVisible_notVisibleToOthers() {
    // given
    Bookmark bookmark = mock(Bookmark.class);
    when(bookmark.getBookmarkId()).thenReturn(2L);
    when(bookmarkRepository.existsByBookmarkIdAndMember_Id(2L, 43L)).thenReturn(false);
    when(bookmark.isVisibleToOthers()).thenReturn(false);

    // when + then
    assertThatThrownBy(() -> BookmarkPermissionService.isVisible(bookmark, 43L))
        .isInstanceOf(AccessDeniedBookmarkException.class);

    verify(bookmarkRepository).existsByBookmarkIdAndMember_Id(2L, 43L);
    verify(bookmark).isVisibleToOthers();
  }

  @Test
  void isVisible_visibleToOthersAndNotOwner() {
    // given
    Bookmark bookmark = mock(Bookmark.class);
    when(bookmark.getBookmarkId()).thenReturn(3L);
    when(bookmarkRepository.existsByBookmarkIdAndMember_Id(3L, 44L)).thenReturn(false);
    when(bookmark.isVisibleToOthers()).thenReturn(true);

    // when + then
    assertThatCode(() -> BookmarkPermissionService.isVisible(bookmark, 44L))
        .doesNotThrowAnyException();

    verify(bookmarkRepository).existsByBookmarkIdAndMember_Id(3L, 44L);
    verify(bookmark).isVisibleToOthers();
  }

  // --- isEditable tests ---

  @Test
  void isEditable_viewerIsNotOwner() {
    // given
    Bookmark bookmark = mock(Bookmark.class);
    Member owner = mock(Member.class);
    when(bookmark.getMember()).thenReturn(owner);
    when(owner.getId()).thenReturn(10L);

    // when + then
    assertThatThrownBy(() -> BookmarkPermissionService.isEditable(bookmark, 20L))
        .isInstanceOf(AccessDeniedBookmarkModifyException.class);

    verify(bookmark).getMember();
    verify(owner).getId();
  }

  @Test
  void isEditable_viewerIsOwner() {
    // given
    Bookmark bookmark = mock(Bookmark.class);
    Member owner = mock(Member.class);
    when(bookmark.getMember()).thenReturn(owner);
    when(owner.getId()).thenReturn(30L);

    // when + then
    assertThatCode(() -> BookmarkPermissionService.isEditable(bookmark, 30L))
        .doesNotThrowAnyException();

    verify(bookmark).getMember();
    verify(owner).getId();
  }
}
