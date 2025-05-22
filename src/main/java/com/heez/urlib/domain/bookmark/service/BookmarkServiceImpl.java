package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkServiceImpl implements BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final MemberService memberService;
  private final TagService tagService;
  private final LinkService linkService;

  @Override
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

  @Override
  public BookmarkDetailResponse getBookmark(Long memberId, Long bookmarkId) {
    Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
        .orElseThrow(BookmarkNotFoundException::new);

    // 북마크 공개 여부 확인
    if (!bookmark.isVisibleToOthers()
        && bookmarkRepository.existsByBookmarkIdAndMember_Id(bookmarkId, memberId)) {
      throw new AccessDeniedBookmarkException();
    }

    List<String> tags = tagService.getTagTitlesByBookmarkId(bookmarkId);

    List<LinkDetailResponse> links = linkService.findLinksByBookmarkId(bookmarkId)
        .stream()
        .map(LinkDetailResponse::from)
        .toList();

    return BookmarkDetailResponse.from(bookmark, tags, links, bookmark.getMember().getId());
  }
}
