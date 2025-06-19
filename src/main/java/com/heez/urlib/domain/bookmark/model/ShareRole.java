package com.heez.urlib.domain.bookmark.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShareRole {
  BOOKMARK_EDITOR("BOOKMARK_EDITOR","편집 가능"),
  BOOKMARK_VIEWER("BOOKMARK_VIEWER","읽기 전용");

  private final String key;
  private final String title;
}
