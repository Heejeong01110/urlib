package com.heez.urlib.domain.tag.model;


import com.heez.urlib.global.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "hashtag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "hashtag_id", nullable = false)
  private Long hashtagId;

  @Column(nullable = false, name = "title")
  private String title;

  @OneToMany(mappedBy = "hashtag", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BookmarkHashtag> bookmarkHashtags = new ArrayList<>();

  @Builder
  public Hashtag(String title, List<BookmarkHashtag> bookmarkHashtags) {
    this.title = title;
    this.bookmarkHashtags = (bookmarkHashtags != null) ? bookmarkHashtags : new ArrayList<>();
  }
}
