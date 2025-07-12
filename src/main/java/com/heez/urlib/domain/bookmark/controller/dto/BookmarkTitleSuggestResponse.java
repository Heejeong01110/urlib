package com.heez.urlib.domain.bookmark.controller.dto;

import lombok.Builder;

@Builder
public record BookmarkTitleSuggestResponse(
    String recommend
) {

}
