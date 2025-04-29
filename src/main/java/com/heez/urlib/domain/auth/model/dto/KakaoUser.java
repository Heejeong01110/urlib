package com.heez.urlib.domain.auth.model.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.heez.urlib.domain.member.model.vo.Nickname;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class KakaoUser {

  private Long id;
  private Properties properties;
  private KakaoAccount kakaoAccount;

  public KakaoUser() {
  }

  public KakaoUser(Long id, Properties properties, KakaoAccount kakaoAccount) {
    this.id = id;
    this.properties = properties;
    this.kakaoAccount = kakaoAccount;
  }

  @Getter
  @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
  public static class Properties {

    private Nickname nickname;
    private String imageUrl;

    public Properties() {
    }

    public Properties(Nickname nickname) {
      this.nickname = nickname;
    }

  }

  @Getter
  public static class KakaoAccount {

    private String email;

    public KakaoAccount() {
    }

    public KakaoAccount(String email) {
      this.email = email;
    }

  }

}
