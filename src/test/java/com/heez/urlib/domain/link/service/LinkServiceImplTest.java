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

  @Test
  void ensureLinks_reusesExistingAndCreatesNew() {
    // given
    Long bookmarkId = 1L;
    Link existing = Link.builder()
        .title("Example")
        .url("http://example.com")
        .build();
    given(linkRepository.findAllByBookmark_BookmarkId(bookmarkId)).willReturn(List.of(existing));

    List<BaseLinkRequest> requests = List.of(
        new BaseLinkRequest("Example", "http://example.com"),
        new BaseLinkRequest("New", "http://new.com")
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

  @Test
  void ensureLinks_normalizesUrlAndReusesMatching() {
    // given
    Long bookmarkId = 1L;
    // 기존 링크에는 소문자, 트레일링 슬래시 제거된 URL 로 저장되어 있음
    Link existing = Link.builder()
        .title("Example")
        .url("http://example.com/path")
        .build();
    given(linkRepository.findAllByBookmark_BookmarkId(bookmarkId))
        .willReturn(List.of(existing));

    // 요청에는 대문자 스킴·호스트·경로 끝에 슬래시 포함
    BaseLinkRequest req = new BaseLinkRequest("Example", "HTTP://EXAMPLE.com/path/");

    // when
    List<Link> result = linkService.ensureLinks(bookmarkId, List.of(req));

    // then
    // normalizeUrl 로 매칭하여 기존 객체를 재사용해야 함
    assertThat(result).hasSize(1)
        .first().isSameAs(existing);
  }
}

