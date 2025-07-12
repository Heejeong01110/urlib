package com.heez.urlib.domain.bookmark.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record BookmarkTitleSuggestRequest(
    @Schema(description = "링크 타이틀 목록", example = "LINK_TITLE_LIST")
    List<String> titles
) {

}
