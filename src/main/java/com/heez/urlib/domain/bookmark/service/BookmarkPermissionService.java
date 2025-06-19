package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkShareRequest;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkModifyException;
import com.heez.urlib.domain.bookmark.exception.BookmarkNotFoundException;
import com.heez.urlib.domain.bookmark.exception.BookmarkShareNotFoundException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.model.ShareRole;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.bookmark.repository.BookmarkShareRepository;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.service.MemberService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkPermissionService {

  private final BookmarkRepository bookmarkRepository;
  private final BookmarkShareRepository bookmarkShareRepository;
  private final MemberService memberService;

  public void isVisible(Bookmark bookmark, Optional<Long> viewerId) {
    boolean isOwner = viewerId.map(id -> bookmark.getMember().getMemberId().equals(id))
        .orElse(false);
    boolean isShared = bookmark.getBookmarkShares().stream().anyMatch(
        item -> item.getMember().getMemberId().equals(viewerId)
            && (item.getRole().equals(ShareRole.BOOKMARK_EDITOR)
            || item.getRole().equals(ShareRole.BOOKMARK_VIEWER)));
    if (bookmark.isVisibleToOthers() || isOwner || isShared) {
      return;
    }
    throw new AccessDeniedBookmarkException();
  }

  public void isEditable(Bookmark bookmark, Long viewerId) {
    boolean isShared = bookmark.getBookmarkShares().stream().anyMatch(
        item -> item.getMember().getMemberId().equals(viewerId)
            && (item.getRole().equals(ShareRole.BOOKMARK_EDITOR)));
    if (bookmark.getMember().getMemberId().equals(viewerId) || isShared) {
      return;
    }
    throw new AccessDeniedBookmarkModifyException();
  }

  public void isOwner(Bookmark bookmark, Long viewerId) {
    if (!bookmark.getMember().getMemberId().equals(viewerId)) {
      throw new AccessDeniedBookmarkModifyException();
    }
  }

  @Transactional
  public void updateBookmarkShare(Long bookmarkId, Long memberId, BookmarkShareRequest request) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    isEditable(bookmark, memberId);

    Member member = memberService.findById(request.memberId());
    bookmark.shareWith(member, request.role());
  }

  @Transactional
  public void deleteBookmarkShare(Long bookmarkId, Long ownerId, Long targetId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    isEditable(bookmark, ownerId);
    bookmark.getBookmarkShares()
        .removeIf(share -> share.getMember().getMemberId().equals(targetId));
  }

  @Transactional
  public void leaveBookmarkShare(Long bookmarkId, Long memberId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    boolean removed = bookmark.getBookmarkShares()
        .removeIf(share -> share.getMember().getMemberId().equals(memberId));
    if (!removed) {
      throw new BookmarkShareNotFoundException();
    }
  }
}
