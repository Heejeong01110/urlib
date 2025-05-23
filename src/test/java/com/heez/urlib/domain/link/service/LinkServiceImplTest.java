package com.heez.urlib.domain.link.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.link.repository.LinkRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkServiceImplTest {

  @Mock
  private LinkRepository linkRepository;

  @InjectMocks
  private LinkServiceImpl linkService;

  @Test
  void findLinksByBookmarkId_returnsLinks() {
    // given
    Long bookmarkId = 123L;
    Link link1 = new Link(1L, "http://a.com", "A", null);
    Link link2 = new Link(2L, "http://b.com", "B", null);
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
}
