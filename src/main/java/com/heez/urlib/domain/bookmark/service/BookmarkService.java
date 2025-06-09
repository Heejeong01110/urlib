package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkSummaryResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkUpdateRequest;
import com.heez.urlib.domain.bookmark.exception.BookmarkNotFoundException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.link.controller.dto.LinkDetailResponse;
import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.link.service.LinkService;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.service.MemberService;
import com.heez.urlib.domain.tag.model.Hashtag;
import com.heez.urlib.domain.tag.service.TagService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final MemberService memberService;
  private final BookmarkPermissionService bookmarkPermissionService;
  private final TagService tagService;
  private final LinkService linkService;

  @Transactional
  public BookmarkCreateResponse createBookmark(Long memberId, BookmarkCreateRequest request) {
    Member member = memberService.findById(memberId);

    Bookmark bookmark = Bookmark.builder()
        .title(request.title())
        .description(request.description())
        .imageUrl(request.imageUrl())
        .visibleToOthers(request.visibleToOthers())
        .member(member)
        .build();

    //태그 목록 추가
    List<Hashtag> hashtags = tagService.ensureTags(request.tags());
    hashtags.forEach(bookmark::addHashtag);

    //링크 목록 추가
    request.links().stream()
        .map(link -> Link.builder()
            .url(link.url())
            .title(link.title())
            .build())
        .forEach(bookmark::addLink);

    Bookmark save = bookmarkRepository.save(bookmark);
    return BookmarkCreateResponse.from(save);
  }

  public BookmarkDetailResponse getBookmark(Optional<Long> memberId, Long bookmarkId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    bookmarkPermissionService.isVisible(bookmark, memberId);

    List<String> tags = tagService.getTagTitlesByBookmarkId(bookmarkId);

    List<LinkDetailResponse> links = linkService.findLinksByBookmarkId(bookmarkId)
        .stream()
        .map(LinkDetailResponse::from)
        .toList();
    addBookmarkViewCount(bookmarkId);
    return BookmarkDetailResponse.from(bookmark, tags, links, bookmark.getMember().getMemberId());
  }

  @Transactional
  public void addBookmarkViewCount(Long bookmarkId) {
    bookmarkRepository.incrementViewCount(bookmarkId);
  }

  @Transactional
  public BookmarkDetailResponse updateBookmark(
      Long memberId,
      Long bookmarkId,
      BookmarkUpdateRequest request) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    bookmarkPermissionService.isEditable(bookmark, memberId);

    bookmark.changeTitle(request.title());
    bookmark.changeDescription(request.description());
    bookmark.changeImageUrl(request.imageUrl());
    bookmark.changeVisibleToOthers(request.visibleToOthers());

    List<Hashtag> tags = tagService.ensureTags(request.tags());
    bookmark.replaceHashtags(tags);
    List<Link> links = linkService.ensureLinks(bookmarkId, request.links());
    bookmark.replaceLinks(links);

    return BookmarkDetailResponse.from(bookmark,
        bookmark.getBookmarkHashtags()
            .stream()
            .map(tag -> tag.getHashtag().getTitle())
            .toList(),
        bookmark.getLinks()
            .stream()
            .map(LinkDetailResponse::from)
            .toList(),
        memberId);
  }

  @Transactional
  public void deleteBookmark(Long memberId, Long bookmarkId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);
    bookmarkPermissionService.isEditable(bookmark, memberId);
    bookmarkRepository.delete(bookmark);
  }

  public Page<BookmarkSummaryResponse> getBookmarkSummaryListByMemberId(
      Optional<Long> viewerId, Long ownerId, Pageable pageable) {
    return viewerId.map(
            id -> bookmarkRepository.findPageByMemberAndViewer(ownerId, id, pageable)
                .map(BookmarkSummaryResponse::from))
        .orElseGet(() -> bookmarkRepository.findPageByMember(ownerId, pageable)
            .map(BookmarkSummaryResponse::from));
  }

  public Page<BookmarkSummaryResponse> getBookmarkSummaryList(Optional<Long> viewerId,
      Pageable pageable) {
    return viewerId.map(id -> bookmarkRepository.findPageByViewer(id, pageable)
            .map(BookmarkSummaryResponse::from))
        .orElseGet(() -> bookmarkRepository.findPageByAnonymous(pageable)
            .map(BookmarkSummaryResponse::from));
  }
}
