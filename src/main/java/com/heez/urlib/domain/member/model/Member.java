package com.heez.urlib.domain.member.model;


import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.model.vo.Nickname;
import com.heez.urlib.global.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id", nullable = false)
  private Long id;

  @Embedded
  @Column(name = "email", unique = true)
  private Email email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "provider_type")
  private AuthType oauthType;

  @Column(name = "identifier", unique = true)
  private String identifier;

  @Column(name = "password")
  private String password;

  @Embedded
  @Column(unique = true, name = "nickname")
  private Nickname nickname;

  @Column(name = "description")
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private Role role;

  @Builder
  public Member(Email email, AuthType oauthType, String identifier, String password,
      Nickname nickname, String description, String imageUrl, Role role) {
    this.email = email;
    this.oauthType = oauthType;
    this.identifier = identifier;
    this.password = password;
    this.nickname = nickname;
    this.description = description;
    this.imageUrl = imageUrl;
    this.role = role;
  }

  public String getNickName() {
    if (nickname == null) {
      return "";
    }

    return nickname.getValue();
  }

  public String getEmail() {
    if (email == null) {
      return "";
    }

    return email.getValue();
  }

}
