package com.heez.urlib.domain.link.service;

import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import com.heez.urlib.domain.link.model.Link;
import java.util.List;

public interface LinkService {

  List<Link> findLinksByBookmarkId(Long bookmarkId);

  List<Link> ensureLinks(Long bookmarkId, List<BaseLinkRequest> requests);
}
