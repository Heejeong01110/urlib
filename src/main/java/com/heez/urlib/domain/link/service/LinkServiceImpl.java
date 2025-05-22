package com.heez.urlib.domain.link.service;

import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.link.repository.LinkRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LinkServiceImpl implements LinkService {

  private final LinkRepository linkRepository;

  @Override
  public List<Link> findLinksByBookmarkId(Long bookmarkId) {
    return linkRepository.findAllByBookmark_BookmarkId(bookmarkId);
  }
}
