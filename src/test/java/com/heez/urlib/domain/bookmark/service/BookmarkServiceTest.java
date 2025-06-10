package com.heez.urlib.domain.bookmark.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkSummaryResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkUpdateRequest;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkModifyException;
import com.heez.urlib.domain.bookmark.exception.BookmarkNotFoundException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.bookmark.service.dto.BookmarkSummaryProjection;
import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import com.heez.urlib.domain.link.controller.dto.LinkDetailResponse;
import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.link.service.LinkService;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.service.MemberService;
import com.heez.urlib.domain.tag.model.Hashtag;
import com.heez.urlib.domain.tag.service.TagService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

  @Mock
  private BookmarkRepository bookmarkRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private TagService tagService;

  @Mock
  private BookmarkPermissionService bookmarkPermissionService;

  @Mock
  private LinkService linkService;

  @InjectMocks
  private BookmarkService bookmarkService;

  @Test
  void createBookmark_callsDependenciesAndReturnsResponse() {
    // given
    Long memberId = 1L;
    List<String> tagNames = List.of("spring", "java");
    BaseLinkRequest linkReq = BaseLinkRequest.builder()
        .title("Example")
        .url("http://example.com")
        .build();
    BookmarkCreateRequest request = BookmarkCreateRequest.builder()
        .imageUrl("http://image_url.example.com")
        .title("My Bookmark")
        .tags(tagNames)
        .description("A description")
        .visibleToOthers(true)
        .links(List.of(linkReq))
        .build();

    Member member = Member.builder()
        .email(new Email("user@example.com"))
        .build();
    ReflectionTestUtils.setField(member, "memberId", memberId);

    given(memberService.findById(memberId)).willReturn(member);

    Hashtag hashtag1 = Hashtag.builder().title("spring").build();
    Hashtag hashtag2 = Hashtag.builder().title("java").build();
    ReflectionTestUtils.setField(hashtag1, "hashtagId", 1L);
    ReflectionTestUtils.setField(hashtag2, "hashtagId", 2L);
    List<Hashtag> hashtags = List.of(hashtag1, hashtag2);

    given(tagService.ensureTags(tagNames)).willReturn(hashtags);

    Bookmark saved = Bookmark.builder()
        .title(request.title())
        .description(request.description())
        .imageUrl(request.imageUrl())
        .visibleToOthers(request.visibleToOthers())
        .member(member)
        .build();
    ReflectionTestUtils.setField(saved, "bookmarkId", 100L);

    hashtags.forEach(saved::addHashtag);
    Link linkEntity = Link.builder()
        .url(linkReq.url())
        .title(linkReq.title())
        .build();
    saved.addLink(linkEntity);

    given(bookmarkRepository.save(any(Bookmark.class))).willReturn(saved);

    // when
    BookmarkCreateResponse response = bookmarkService.createBookmark(memberId, request);

    // then
    assertThat(response.id()).isEqualTo(100L);
    assertThat(response.title()).isEqualTo(request.title());
    assertThat(response.description()).isEqualTo(request.description());
    assertThat(response.imageUrl()).isEqualTo(request.imageUrl());
    assertThat(response.visibleToOthers()).isTrue();
    assertThat(response.tags()).containsExactlyElementsOf(tagNames);

    assertThat(response.links())
        .hasSize(1)
        .extracting("url")
        .containsExactly(linkReq.url());

    then(memberService).should().findById(memberId);
    then(tagService).should().ensureTags(tagNames);
    then(bookmarkRepository).should().save(any(Bookmark.class));
  }

  @Test
  void getBookmark_success() {
    // given
    Member owner = mock(Member.class);
    given(owner.getMemberId()).willReturn(999L);
    Long bookmarkId = 100L;
    Long memberId = 100L;

    List<String> tags = List.of("a", "b");
    Bookmark bookmark = Bookmark.builder()
        .title("T")
        .description("D")
        .imageUrl("U")
        .visibleToOthers(true)
        .viewCount(5L)
        .member(owner)
        .build();
    Bookmark updated = Bookmark.builder()
        .title("T")
        .description("D")
        .imageUrl("U")
        .visibleToOthers(true)
        .viewCount(6L)
        .member(owner)
        .build();
    Link link = Link.builder()
        .title("L1")
        .url("URL1")
        .build();
    ReflectionTestUtils.setField(link, "linkId", 11L);
    ReflectionTestUtils.setField(bookmark, "bookmarkId", bookmarkId);
    ReflectionTestUtils.setField(updated, "bookmarkId", bookmarkId);

    given(bookmarkRepository.findById(bookmarkId))
        .willReturn(Optional.of(bookmark))
        .willReturn(Optional.of(updated));
    doNothing().when(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(tagService.getTagTitlesByBookmarkId(bookmarkId)).willReturn(tags);
    given(linkService.findLinksByBookmarkId(bookmarkId)).willReturn(List.of(link));

    // when
    BookmarkDetailResponse result = bookmarkService.getBookmark(Optional.of(memberId), bookmarkId);

    // then
    assertThat(result.id()).isEqualTo(bookmarkId);
    assertThat(result.title()).isEqualTo("T");
    assertThat(result.description()).isEqualTo("D");
    assertThat(result.imageUrl()).isEqualTo("U");
    assertThat(result.visibleToOthers()).isTrue();
    assertThat(result.viewCount()).isEqualTo(5L + 1);
    assertThat(result.tags()).containsExactly("a", "b");
    assertThat(result.links())
        .extracting(LinkDetailResponse::id, LinkDetailResponse::title, LinkDetailResponse::url)
        .containsExactly(tuple(11L, "L1", "URL1"));
    assertThat(result.writerId()).isEqualTo(999L);
  }

  @Test
  void updateBookmark_success() {
    // given
    Long bookmarkId = 7L;
    Long memberId = 7L;
    Member owner = mock(Member.class);

    Bookmark bookmark = Bookmark.builder()
        .title("old")
        .description("old-desc")
        .imageUrl("old-img")
        .visibleToOthers(false)
        .member(owner)
        .build();
    ReflectionTestUtils.setField(bookmark, "bookmarkId", bookmarkId);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    doNothing().when(bookmarkPermissionService).isEditable(bookmark, memberId);

    List<String> tagsReq = List.of("spring", "java");
    List<BaseLinkRequest> linksReq = List.of(
        BaseLinkRequest.builder().title("Example").url("http://example.com").build());
    BookmarkUpdateRequest req = new BookmarkUpdateRequest(
        "new-title",
        "new-desc",
        "new-img",
        true,
        tagsReq,
        linksReq
    );

    Hashtag hashtag1 = Hashtag.builder().title("spring").build();
    Hashtag hashtag2 = Hashtag.builder().title("java").build();
    ReflectionTestUtils.setField(hashtag1, "hashtagId", 1L);
    ReflectionTestUtils.setField(hashtag2, "hashtagId", 2L);
    List<Hashtag> tags = List.of(hashtag1, hashtag2);
    given(tagService.ensureTags(tagsReq)).willReturn(tags);

    Link link = Link.builder()
        .title("Example")
        .url("http://example.com")
        .build();
    ReflectionTestUtils.setField(link, "linkId", 1L);
    List<Link> links = List.of(link);
    given(linkService.ensureLinks(bookmarkId, linksReq)).willReturn(links);

    // when
    BookmarkDetailResponse result = bookmarkService.updateBookmark(memberId, bookmarkId, req);

    // then
    assertThat(result.title()).isEqualTo("new-title");
    assertThat(result.description()).isEqualTo("new-desc");
    assertThat(result.imageUrl()).isEqualTo("new-img");
    assertThat(result.visibleToOthers()).isTrue();

    assertThat(result.tags()).containsExactly("spring", "java");
    assertThat(result.links())
        .extracting(LinkDetailResponse::url)
        .containsExactly("http://example.com");
  }

  @Test
  void updateBookmark_notFound() {
    // given
    Long bookmarkId = 7L;
    Long memberId = 7L;
    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        bookmarkService.updateBookmark(memberId, bookmarkId,
            new BookmarkUpdateRequest(
                "t", "d", "i", true, List.of(), List.of()
            )
        )
    ).isInstanceOf(BookmarkNotFoundException.class);
  }

  @Test
  void updateBookmark_accessDenied() {
    // given
    Long bookmarkId = 7L;
    Long memberId = 7L;
    Bookmark bookmark = Bookmark.builder().build();
    ReflectionTestUtils.setField(bookmark, "bookmarkId", bookmarkId);

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    willThrow(new AccessDeniedBookmarkModifyException())
        .given(bookmarkPermissionService)
        .isEditable(bookmark, memberId);

    // when & then
    assertThatThrownBy(() ->
        bookmarkService.updateBookmark(memberId, bookmarkId,
            new BookmarkUpdateRequest(
                "t", "d", "i", true, List.of(), List.of()
            )
        )
    ).isInstanceOf(AccessDeniedBookmarkModifyException.class);

    then(bookmarkRepository).should().findById(bookmarkId);
    then(bookmarkPermissionService).should().isEditable(bookmark, memberId);
  }

  @Test
  void deleteBookmark_success() {
    // given
    Long ownerId = 2L;
    Long bookmarkId = 10L;
    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", ownerId);
    Bookmark bookmark = Bookmark.builder()
        .member(member)
        .build();
    when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(bookmark));
    doNothing().when(bookmarkPermissionService).isEditable(bookmark, ownerId);

    // when
    bookmarkService.deleteBookmark(ownerId, bookmarkId);

    // then
    verify(bookmarkRepository).delete(bookmark);
  }

  @Test
  void deleteBookmark_notFound_throwsBookmarkNotFoundException() {
    // given
    Long ownerId = 2L;
    Long bookmarkId = 10L;
    when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.empty());

    // when + then
    assertThatThrownBy(() -> bookmarkService.deleteBookmark(ownerId, bookmarkId))
        .isInstanceOf(BookmarkNotFoundException.class);

    verify(bookmarkRepository, never()).delete(any());
  }

  @Test
  void deleteBookmark_memberMismatch_throwsAccessDenied() {
    // given
    Long memberId = 1L;
    Long ownerId = 2L;
    Long bookmarkId = 10L;

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", ownerId);
    Bookmark bookmark = Bookmark.builder()
        .member(member)
        .build();
    when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(bookmark));
    willThrow(new AccessDeniedBookmarkModifyException())
        .given(bookmarkPermissionService)
        .isEditable(bookmark, memberId);

    // when + then
    assertThatThrownBy(() -> bookmarkService.deleteBookmark(memberId, bookmarkId))
        .isInstanceOf(AccessDeniedBookmarkModifyException.class);

    verify(bookmarkRepository, never()).delete(any());
  }

  @Test
  void getBookmarkSummaryListByMemberId_returnsMappedResponses() {
    // given
    Long viewerId = 1L;
    Long ownerId = 2L;
    Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

    BookmarkSummaryProjection projection = mock(BookmarkSummaryProjection.class);
    when(projection.getBookmarkId()).thenReturn(10L);
    when(projection.getTitle()).thenReturn("Sample Title");
    when(projection.getDescription()).thenReturn("Sample Description");
    when(projection.getImageUrl()).thenReturn("http://example.com/bookmark.png");

    BookmarkSummaryProjection.MemberInfo memberInfo = mock(
        BookmarkSummaryProjection.MemberInfo.class);
    when(memberInfo.getId()).thenReturn(ownerId);
    when(memberInfo.getImageUrl()).thenReturn("http://example.com/member.png");
    when(projection.getMember()).thenReturn(memberInfo);

    Page<BookmarkSummaryProjection> repoPage =
        new PageImpl<>(List.of(projection), pageable, 1);

    given(bookmarkRepository.findPageByMemberAndViewer(ownerId, viewerId, pageable))
        .willReturn(repoPage);

    // when
    Page<BookmarkSummaryResponse> result =
        bookmarkService.getBookmarkSummaryListByMemberId(Optional.of(viewerId), ownerId, pageable);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);

    BookmarkSummaryResponse dto = result.getContent().get(0);
    assertThat(dto.id()).isEqualTo(10L);
    assertThat(dto.title()).isEqualTo("Sample Title");
    assertThat(dto.description()).isEqualTo("Sample Description");
    assertThat(dto.bookmarkImageUrl()).isEqualTo("http://example.com/bookmark.png");
    assertThat(dto.memberSummary().memberId()).isEqualTo(ownerId);
    assertThat(dto.memberSummary().memberImageUrl()).isEqualTo("http://example.com/member.png");
  }

  @Test
  void getBookmarkSummaryList_returnsMappedResponses() {
    // given
    Long viewerId = 1L;
    Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

    BookmarkSummaryProjection projection = mock(BookmarkSummaryProjection.class);
    when(projection.getBookmarkId()).thenReturn(42L);
    when(projection.getTitle()).thenReturn("Overview Title");
    when(projection.getDescription()).thenReturn("Overview Description");
    when(projection.getImageUrl()).thenReturn("http://example.com/overview.png");

    BookmarkSummaryProjection.MemberInfo memberInfo = mock(
        BookmarkSummaryProjection.MemberInfo.class);
    when(memberInfo.getId()).thenReturn(99L);
    when(memberInfo.getImageUrl()).thenReturn("http://example.com/user.png");
    when(projection.getMember()).thenReturn(memberInfo);

    Page<BookmarkSummaryProjection> repoPage = new PageImpl<>(List.of(projection), pageable, 1);

    given(bookmarkRepository.findPageByViewer(viewerId, pageable)).willReturn(repoPage);

    // when
    Page<BookmarkSummaryResponse> result =
        bookmarkService.getBookmarkSummaryList(Optional.of(viewerId), pageable);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);

    BookmarkSummaryResponse dto = result.getContent().get(0);
    assertThat(dto.id()).isEqualTo(42L);
    assertThat(dto.title()).isEqualTo("Overview Title");
    assertThat(dto.description()).isEqualTo("Overview Description");
    assertThat(dto.bookmarkImageUrl()).isEqualTo("http://example.com/overview.png");
    assertThat(dto.memberSummary().memberId()).isEqualTo(99L);
    assertThat(dto.memberSummary().memberImageUrl()).isEqualTo("http://example.com/user.png");
  }
}
