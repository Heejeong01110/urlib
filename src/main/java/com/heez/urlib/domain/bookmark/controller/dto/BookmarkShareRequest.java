package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.bookmark.model.ShareRole;

public record BookmarkShareRequest(
    Long memberId,
    ShareRole role
) {

}
