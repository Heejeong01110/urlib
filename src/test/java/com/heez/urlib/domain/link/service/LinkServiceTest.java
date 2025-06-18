package com.heez.urlib.domain.link.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.link.repository.LinkRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

  @Mock
  private LinkRepository linkRepository;

  @InjectMocks
  private LinkService linkService;

  @Test
  void findLinksByBookmarkId_returnsLinks() {
    // given
    Long bookmarkId = 123L;
    Link link1 = new Link( "http://a.com", "A", null);
    Link link2 = new Link( "http://b.com", "B", null);
    ReflectionTestUtils.setField(link1, "linkId", 1L);
    ReflectionTestUtils.setField(link2, "linkId", 2L);
    given(linkRepository.findAllByBookmark_BookmarkId(bookmarkId))
        .willReturn(List.of(link1, link2));

    // when
    List<Link> result = linkService.findLinksByBookmarkId(bookmarkId);

    // then
    assertThat(result).containsExactly(link1, link2);
    then(linkRepository)
        .should().findAllByBookmark_BookmarkId(bookmarkId);
  }

  @Test
  void findLinksByBookmarkId_returnsEmptyListWhenNoLinks() {
    // given
    Long bookmarkId = 456L;
    given(linkRepository.findAllByBookmark_BookmarkId(bookmarkId))
        .willReturn(List.of());

    // when
    List<Link> result = linkService.findLinksByBookmarkId(bookmarkId);

    // then
    assertThat(result).isEmpty();
    then(linkRepository).should().findAllByBookmark_BookmarkId(bookmarkId);
  }

  @Test
  void ensureLinks_reusesExistingAndCreatesNew() {
    // given
    Long bookmarkId = 1L;
    Link existing = Link.builder()
        .title("Example")
        .url("http://example.com")
        .build();
    ReflectionTestUtils.setField(existing, "linkId", 1L);
    given(linkRepository.findAllByBookmark_BookmarkId(bookmarkId)).willReturn(List.of(existing));

    List<BaseLinkRequest> requests = List.of(
        new BaseLinkRequest(1L,"Example", "http://example.com"),
        new BaseLinkRequest(null,"New", "http://new.com")
    );

    // when
    List<Link> result = linkService.ensureLinks(bookmarkId, requests);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isSameAs(existing);
    assertThat(result.get(1)).isNotSameAs(existing)
        .extracting(Link::getTitle, Link::getUrl)
        .containsExactly("New", "http://new.com");
  }

}

