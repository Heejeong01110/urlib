package com.heez.urlib.domain.bookmark.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.link.controller.dto.LinkCreateRequest;
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

@ExtendWith(MockitoExtension.class)
class BookmarkServiceImplTest {

  @Mock
  private BookmarkRepository bookmarkRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private TagService tagService;

  @Mock
  private LinkService linkService;

  @InjectMocks
  private BookmarkServiceImpl bookmarkService;

  @Test
  void createBookmark_callsDependenciesAndReturnsResponse() {
    // given
    Long memberId = 1L;
    List<String> tagNames = List.of("spring", "java");
    LinkCreateRequest linkReq = LinkCreateRequest.builder()
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
        .id(memberId)
        .email(new Email("user@example.com"))
        .build();

    given(memberService.findById(memberId)).willReturn(member);

    List<Hashtag> hashtags = List.of(
        Hashtag.builder().hashtagId(1L).title("spring").build(),
        Hashtag.builder().hashtagId(2L).title("java").build()
    );
    given(tagService.ensureTags(tagNames)).willReturn(hashtags);

    Bookmark saved = Bookmark.builder()
        .bookmarkId(100L)
        .title(request.title())
        .description(request.description())
        .imageUrl(request.imageUrl())
        .visibleToOthers(request.visibleToOthers())
        .member(member)
        .build();

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
  void getBookmark() {
    // given
    Member owner = Member.builder()
        .id(999L)
        .build();
    Long bookmarkId = 100L;
    Long memberId = 100L;

    Bookmark bookmark = Bookmark.builder()
        .bookmarkId(bookmarkId)
        .title("T")
        .description("D")
        .imageUrl("U")
        .visibleToOthers(true)
        .viewCount(5L)
        .member(owner)
        .build();
    when(bookmarkRepository.findById(bookmarkId))
        .thenReturn(Optional.of(bookmark));

    List<String> tags = List.of("a", "b");
    when(tagService.getTagTitlesByBookmarkId(bookmarkId)).thenReturn(tags);

    Link link1 = mock(Link.class);
    when(link1.getLinkId()).thenReturn(11L);
    when(link1.getTitle()).thenReturn("L1");
    when(link1.getUrl()).thenReturn("URL1");
    when(linkService.findLinksByBookmarkId(bookmarkId))
        .thenReturn(List.of(link1));

    // when
    BookmarkDetailResponse result = bookmarkService.getBookmark(memberId, bookmarkId);

    // then
    assertThat(result.id()).isEqualTo(bookmarkId);
    assertThat(result.title()).isEqualTo("T");
    assertThat(result.description()).isEqualTo("D");
    assertThat(result.imageUrl()).isEqualTo("U");
    assertThat(result.visibleToOthers()).isTrue();
    assertThat(result.viewCount()).isEqualTo(5L);
    assertThat(result.tags()).containsExactly("a", "b");
    assertThat(result.links())
        .extracting(LinkDetailResponse::id, LinkDetailResponse::title, LinkDetailResponse::url)
        .containsExactly(tuple(11L, "L1", "URL1"));
    assertThat(result.writerId()).isEqualTo(999L);
  }
}
