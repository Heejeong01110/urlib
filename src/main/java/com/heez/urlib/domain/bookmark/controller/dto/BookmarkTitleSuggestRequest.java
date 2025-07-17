package com.heez.urlib.domain.bookmark.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BookmarkTitleSuggestRequest(
    @Schema(description = "링크 타이틀 목록", example = "LINK_TITLE_LIST")
    @NotEmpty(message = "제목 리스트는 비어 있을 수 없습니다.")
    List<String> titles
) {

}
