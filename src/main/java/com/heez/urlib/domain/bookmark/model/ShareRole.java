package com.heez.urlib.domain.bookmark.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "북마크 공유 권한 (EDITOR: 편집 가능, VIEWER: 읽기 전용)")
@Getter
@RequiredArgsConstructor
public enum ShareRole {
  BOOKMARK_EDITOR("BOOKMARK_EDITOR", "편집 가능"),
  BOOKMARK_VIEWER("BOOKMARK_VIEWER", "읽기 전용");

  private final String key;
  private final String title;
}
