package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.model.ShareRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record BookmarkShareRequest(

    @Schema(description = "공유할 대상 회원 ID", example = "10")
    Long memberId,

    @Schema(description = "공유 권한", example = "BOOKMARK_EDITOR")
    ShareRole role
) {

}
