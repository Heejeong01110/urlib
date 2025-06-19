package com.heez.urlib.domain.bookmark.model;

import com.heez.urlib.domain.link.model.Link;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.tag.model.BookmarkHashtag;
import com.heez.urlib.domain.tag.model.Hashtag;
import com.heez.urlib.global.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bookmark_id", nullable = false)
  private Long bookmarkId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(nullable = false, name = "description")
  private String description;

  @Column(nullable = false, name = "image_url")
  private String imageUrl;

  @Column(nullable = false, name = "visible_to_others")
  private boolean visibleToOthers;

  @Column(nullable = false, name = "view_count")
  private Long viewCount;

  @Column(nullable = false, name = "like_count")
  private Long likeCount;

  @OneToMany(mappedBy = "bookmark", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BookmarkHashtag> bookmarkHashtags = new ArrayList<>();

  @OneToMany(mappedBy = "bookmark", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Link> links = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @OneToMany(mappedBy = "bookmark", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BookmarkShare> bookmarkShares = new ArrayList<>();

  @Builder
  public Bookmark(String title, String description, String imageUrl, boolean visibleToOthers,
      Long viewCount, Long likeCount, List<BookmarkHashtag> bookmarkHashtags, List<Link> links,
      Member member, List<BookmarkShare> bookmarkShares) {
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
    this.visibleToOthers = visibleToOthers;
    this.viewCount = viewCount;
    this.likeCount = likeCount;
    this.bookmarkHashtags = (bookmarkHashtags != null) ? bookmarkHashtags : new ArrayList<>();
    this.links = (links != null) ? links : new ArrayList<>();
    this.member = member;
    this.bookmarkShares = (bookmarkShares != null) ? bookmarkShares : new ArrayList<>();
  }

  public void changeTitle(String title) {
    this.title = title;
  }

  public void changeDescription(String description) {
    this.description = description;
  }

  public void changeImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void changeVisibleToOthers(boolean visibleToOthers) {
    this.visibleToOthers = visibleToOthers;
  }

  public void addHashtag(Hashtag tag) {
    BookmarkHashtag bh = BookmarkHashtag.builder()
        .bookmark(this)
        .hashtag(tag)
        .build();
    bookmarkHashtags.add(bh);
    tag.getBookmarkHashtags().add(bh);
  }

  public void addLink(Link link) {
    links.add(link);
    link.setBookmark(this);
  }

  public void replaceHashtags(List<Hashtag> tags) {
    this.bookmarkHashtags.clear();
    tags.forEach(this::addHashtag);
  }

  public void replaceLinks(List<Link> links) {
    this.links.clear();
    links.forEach(this::addLink);
  }

  public void shareWith(Member member, ShareRole role) {
    BookmarkShare bookmarkShare = bookmarkShares.stream()
        .filter(share -> share.getMember().equals(member))
        .findFirst()
        .orElseGet(() -> {
          BookmarkShare newShare = BookmarkShare.builder()
              .bookmark(this)
              .member(member)
              .role(role)
              .build();
          bookmarkShares.add(newShare);
          return newShare;
        });
    bookmarkShare.changeRole(role);
  }
}
