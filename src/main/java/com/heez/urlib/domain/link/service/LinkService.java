package com.heez.urlib.domain.link.service;

import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.link.repository.LinkRepository;
import java.net.URI;
import java.net.URISyntaxException;
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

    Map<String, Link> keyToLink = existing.stream()
        .collect(Collectors.toMap(
            link -> buildKey(link.getTitle(), link.getUrl()),
            Function.identity()
        ));

    List<Link> links = new ArrayList<>(requests.size());
    for (BaseLinkRequest req : requests) {
      String key = buildKey(req.title(), req.url());
      Link link = keyToLink.computeIfAbsent(key, (i) -> Link.builder()
          .title(req.title())
          .url(req.url())
          .build());
      links.add(link);
    }
    return links;
  }

  private String buildKey(String title, String url) {
    String t = title == null ? "" : title.trim();
    String u = normalizeUrl(url);
    return t + "||" + u;
  }

  private String normalizeUrl(String raw) {
    try {
      URI uri = new URI(raw.trim());
      String scheme = uri.getScheme().toLowerCase();
      String host = uri.getHost().toLowerCase();
      String path = Optional.ofNullable(uri.getPath()).orElse("/");
      if (path.endsWith("/") && path.length() > 1) {
        path = path.substring(0, path.length() - 1);
      }
      return new URI(
          scheme,
          null,
          host,
          uri.getPort(),
          path,
          uri.getQuery(),
          null
      ).toString();
    } catch (URISyntaxException e) {
      return raw.trim().toLowerCase();
    }
  }
}
