package com.heez.urlib.domain.bookmark.model;

import com.heez.urlib.domain.member.model.Member;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "bookmark_like",
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "bookmark_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkLike extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bookmark_like_id", nullable = false)
  private Long bookmarkLikeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bookmark_id", nullable = false)
  private Bookmark bookmark;

  @Builder
  public BookmarkLike(Member member, Bookmark bookmark) {
    this.member = member;
    this.bookmark = bookmark;
  }
}
