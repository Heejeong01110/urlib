package com.heez.urlib.domain.member;


import com.heez.urlib.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @Column(nullable = false, name = "email")
  private String email;

  @Column(nullable = false, name = "password")
  private String password;

  @Column(nullable = false, name = "nickname")
  private String nickname;

  @Column(nullable = false, name = "description")
  private String description;

  @Column(nullable = false, name = "image_url")
  private String imageUrl;

  @OneToMany(mappedBy = "loginTypeId", fetch = FetchType.LAZY)
  private List<LoginType> loginType;

}
