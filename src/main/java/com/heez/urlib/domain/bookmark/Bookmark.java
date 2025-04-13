package com.heez.urlib.domain.bookmark;

import com.heez.urlib.domain.common.BaseEntity;
import com.heez.urlib.domain.link.Link;
import com.heez.urlib.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bookmark_id", nullable = false)
  private Long bookmarkId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(nullable = false, name = "url")
  private String description;

  @Column(nullable = false, name = "image_url")
  private String imageUrl;

  @Column(nullable = false, name = "is_public")
  private boolean isPublic;

  @Column(nullable = false, name = "view_count")
  private Long viewCount;

  @OneToMany(mappedBy = "bookmarkHashtagId", fetch = FetchType.LAZY)
  private List<BookmarkHashtag> bookmarkHashtags;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
}
