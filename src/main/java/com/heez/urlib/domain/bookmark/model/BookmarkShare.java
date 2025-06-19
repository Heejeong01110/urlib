package com.heez.urlib.domain.bookmark.model;

import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.global.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "bookmark_share")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkShare extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bookmark_share_id", nullable = false)
  private Long bookmarkShareId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bookmark_id", nullable = false)
  private Bookmark bookmark;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ShareRole role;

  @Builder
  public BookmarkShare(Bookmark bookmark, Member member, ShareRole role) {
    this.bookmark = bookmark;
    this.member = member;
    this.role = role;
  }

  public void changeRole(ShareRole role) {
    this.role = role;
  }

}
