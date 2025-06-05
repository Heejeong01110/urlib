package com.heez.urlib.domain.bookmark.service;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkModifyException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.member.model.Member;
import java.util.Optional;
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
  private BookmarkPermissionServiceImpl bookmarkPermissionService;

  @Test
  void isVisible_publicBookmarkAndAnonymousUser_doesNotThrowException() {
    // given
    Bookmark bookmark = mock(Bookmark.class);
    when(bookmark.isVisibleToOthers()).thenReturn(true);

    // when & then
    assertThatCode(() -> bookmarkPermissionService.isVisible(bookmark, Optional.empty()))
        .doesNotThrowAnyException();
  }

  @Test
  void isVisible_privateBookmarkAndOwner_doesNotThrowException() {
    // given
    Member owner = mock(Member.class);
    when(owner.getId()).thenReturn(1L);

    Bookmark bookmark = mock(Bookmark.class);
    when(bookmark.getMember()).thenReturn(owner);
    when(bookmark.isVisibleToOthers()).thenReturn(false);

    // when & then
    assertThatCode(() -> bookmarkPermissionService.isVisible(bookmark, Optional.of(1L)))
        .doesNotThrowAnyException();
  }

  @Test
  void isVisible_privateBookmarkAndNonOwner_throwsAccessDeniedBookmarkException() {
    // given
    Member owner = mock(Member.class);
    when(owner.getId()).thenReturn(1L);

    Bookmark bookmark = mock(Bookmark.class);
    when(bookmark.getMember()).thenReturn(owner);
    when(bookmark.isVisibleToOthers()).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> bookmarkPermissionService.isVisible(bookmark, Optional.of(2L)))
        .isInstanceOf(AccessDeniedBookmarkException.class);
  }

  @Test
  void isEditable_owner_doesNotThrowException() {
    // given
    Member owner = mock(Member.class);
    when(owner.getId()).thenReturn(1L);

    Bookmark bookmark = mock(Bookmark.class);
    when(bookmark.getMember()).thenReturn(owner);

    // when & then
    assertThatCode(() -> bookmarkPermissionService.isEditable(bookmark, 1L))
        .doesNotThrowAnyException();
  }

  @Test
  void isEditable_nonOwner_throwsAccessDeniedBookmarkModifyException() {
    // given
    Member owner = mock(Member.class);
    when(owner.getId()).thenReturn(1L);

    Bookmark bookmark = mock(Bookmark.class);
    when(bookmark.getMember()).thenReturn(owner);

    // when & then
    assertThatThrownBy(() -> bookmarkPermissionService.isEditable(bookmark, 2L))
        .isInstanceOf(AccessDeniedBookmarkModifyException.class);
  }
}
