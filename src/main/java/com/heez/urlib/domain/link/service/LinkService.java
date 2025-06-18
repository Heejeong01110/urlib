package com.heez.urlib.domain.link.service;

import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import com.heez.urlib.domain.link.exception.LinkNotFoundException;
import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.link.repository.LinkRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LinkService {

  private final LinkRepository linkRepository;

  public List<Link> findLinksByBookmarkId(Long bookmarkId) {
    return linkRepository.findAllByBookmark_BookmarkId(bookmarkId);
  }

  @Transactional
  public List<Link> ensureLinks(Long bookmarkId, List<BaseLinkRequest> requests) {
    List<Link> existing = linkRepository.findAllByBookmark_BookmarkId(bookmarkId);
    Map<Long, Link> idToLink = existing.stream()
        .filter(link -> link.getLinkId() != null)
        .collect(Collectors.toMap(Link::getLinkId, Function.identity()));

    List<Link> links = new ArrayList<>();
    for (BaseLinkRequest req : requests) {
      links.add(resolveLink(req, idToLink));
    }
    return links;
  }

  private Link resolveLink(BaseLinkRequest req, Map<Long, Link> idToLink) {
    if(req.id() != null) {
      return Optional.ofNullable(idToLink.get(req.id()))
          .orElseThrow(LinkNotFoundException::new);
    }
    return Link.builder()
        .title(req.title())
        .url(req.url())
        .build();
  }
}
