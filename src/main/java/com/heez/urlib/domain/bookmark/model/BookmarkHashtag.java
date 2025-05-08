package com.heez.urlib.domain.bookmark.model;

import com.heez.urlib.global.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "bookmark_hashtag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkHashtag extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bookmark_hashtag_id", nullable = false)
  private Long bookmarkHashtagId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bookmark_id")
  private Bookmark bookmark;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hashtag_id")
  private Hashtag hashtag;
}
