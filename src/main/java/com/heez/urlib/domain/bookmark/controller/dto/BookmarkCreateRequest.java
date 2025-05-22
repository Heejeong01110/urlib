package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.link.controller.dto.LinkCreateRequest;
import java.util.List;
import lombok.Builder;

@Builder
public record BookmarkCreateRequest(
    String imageUrl,
    String title,
    List<String> tags,
    String description,
    Boolean visibleToOthers,
    List<LinkCreateRequest> links
) {

}
