package com.heez.urlib.domain.link.service;

import com.heez.urlib.domain.link.controller.dto.LinkCreateRequest;
import com.heez.urlib.domain.link.model.Link;
import java.util.List;

public interface LinkService {
  List<Link> saveLinks(List<LinkCreateRequest> request);
}
