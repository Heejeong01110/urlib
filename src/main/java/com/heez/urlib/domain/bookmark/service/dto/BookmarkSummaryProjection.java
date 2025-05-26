package com.heez.urlib.domain.bookmark.service.dto;

public interface BookmarkSummaryProjection {

  Long getBookmarkId();

  String getTitle();

  String getDescription();

  String getImageUrl();

  MemberInfo getMember();

  interface MemberInfo {

    Long getId();

    String getImageUrl();
  }
}
