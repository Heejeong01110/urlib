package com.heez.urlib.domain.link.service;

import com.heez.urlib.domain.link.model.Link;
import java.util.List;

public interface LinkService {

  List<Link> findLinksByBookmarkId(Long bookmarkId);
}
