package com.heez.urlib.domain.bookmark.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkUpdateRequest;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.repository.BookmarkRepository;
import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import com.heez.urlib.domain.link.controller.dto.LinkDetailResponse;
import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.link.service.LinkService;
import com.heez.urlib.domain.member.model.Member;
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
public class BookmarkServiceImplEntityStateTest {

  @Mock
  private BookmarkRepository bookmarkRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private TagService tagService;

  @Mock
  private LinkService linkService;

  @Mock
  private BookmarkPermissionService bookmarkPermissionService;

  @InjectMocks
  private BookmarkServiceImpl bookmarkService;

  @Test
  void updateBookmark_mutatesEntityCorrectly() {
    Long bookmarkId = 7L;
    Long memberId = 42L;
    // given
    Member member = mock(Member.class);

    Bookmark bookmark = Bookmark.builder()
        .bookmarkId(bookmarkId)
        .title("old-title")
        .description("old-desc")
        .imageUrl("old-img")
        .visibleToOthers(false)
        .member(member)
        .build();

    given(bookmarkRepository.findById(bookmarkId)).willReturn(Optional.of(bookmark));
    doNothing().when(bookmarkPermissionService).isEditable(bookmark, memberId);

    BookmarkUpdateRequest req = new BookmarkUpdateRequest(
        "new-title",
        "new-desc",
        "new-img",
        true,
        List.of("spring", "java"),
        List.of(new BaseLinkRequest("Example", "http://example.com"))
    );

    given(tagService.ensureTags(req.tags())).willReturn(List.of(
        Hashtag.builder().title("spring").build(),
        Hashtag.builder().title("java").build()
    ));

    Link link1 = Link.builder()
        .title("Example")
        .url("http://example.com")
        .build();
    given(linkService.ensureLinks(bookmarkId, req.links()))
        .willReturn(List.of(link1));

    // when
    BookmarkDetailResponse dto = bookmarkService.updateBookmark(memberId, bookmarkId, req);

    // then
    assertThat(bookmark.getTitle()).isEqualTo("new-title");
    assertThat(bookmark.getDescription()).isEqualTo("new-desc");
    assertThat(bookmark.getImageUrl()).isEqualTo("new-img");
    assertThat(bookmark.isVisibleToOthers()).isTrue();

    assertThat(bookmark.getBookmarkHashtags())
        .extracting(bh -> bh.getHashtag().getTitle())
        .containsExactlyInAnyOrder("spring", "java");

    assertThat(bookmark.getLinks())
        .extracting(Link::getTitle, Link::getUrl)
        .containsExactly(tuple("Example", "http://example.com"));

    assertThat(dto.title()).isEqualTo("new-title");
    assertThat(dto.tags()).containsExactlyInAnyOrder("spring", "java");
    assertThat(dto.links())
        .extracting(LinkDetailResponse::title, LinkDetailResponse::url)
        .containsExactly(tuple("Example", "http://example.com"));
  }
}
