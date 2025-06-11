package com.heez.urlib.domain.member.model;


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
@Table(name = "follows",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_follow_follower_following", columnNames = {"follower_id", "following_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "follow_id", nullable = false)
  private Long followId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "follower_id", nullable = false)
  private Member follower; //팔로우 요청자

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "following_id", nullable = false)
  private Member following; //팔로우 대상

  @Builder
  public Follow(Member follower, Member following) {
    this.follower = follower;
    this.following = following;
  }
}
