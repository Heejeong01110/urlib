package com.heez.urlib.domain.bookmark.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.link.controller.dto.LinkCreateRequest;
import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.service.MemberService;
import com.heez.urlib.domain.tag.model.Hashtag;
import com.heez.urlib.domain.tag.service.TagService;
import java.util.List;
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
        Hashtag.builder().hashtagId(1L).name("spring").build(),
        Hashtag.builder().hashtagId(2L).name("java").build()
    );
    given(tagService.ensureTags(tagNames)).willReturn(hashtags);

    Bookmark saved = Bookmark.builder()
        .bookmarkId(100L)
        .name(request.title())
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

    // then: response reflects saved data
    assertEquals(100L, response.id());
    assertEquals(request.title(), response.name());
    assertEquals(request.description(), response.description());
    assertEquals(request.imageUrl(), response.imageUrl());
    assertEquals(request.visibleToOthers(), response.visibleToOthers());
    assertIterableEquals(tagNames, response.tags());
    assertEquals(1, response.links().size());
    assertEquals(linkReq.url(), response.links().get(0).url());

    // verify interactions
    then(memberService).should().findById(memberId);
    then(tagService).should().ensureTags(tagNames);
    then(bookmarkRepository).should().save(any(Bookmark.class));
  }
}
