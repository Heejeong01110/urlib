package com.heez.urlib.domain.link.model;

import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.common.BaseEntity;
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
@Table(name = "link")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Link extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "link_id", nullable = false)
  private Long linkId;

  @Column(nullable = false, name = "link_name")
  private String link_name;

  @Column(nullable = false, name = "url")
  private String url;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bookmark_id", nullable = false)
  private Bookmark bookmark;

}
