package com.heez.urlib.domain.member.model.vo;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nickname {

  public static final int NAME_MAX_LENGTH = 200;

  @Column(name = "nickname")
  private String value;

  public Nickname(String value) {
    validateName(value);
    this.value = value;
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("닉네임은 빈 값일 수 없습니다.");
    }
    if (NAME_MAX_LENGTH < name.length()) {
      throw new IllegalArgumentException(String.format("닉네임은 %d 글자를 넘을 수 없습니다.", NAME_MAX_LENGTH));
    }
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Nickname nickname = (Nickname) o;
    return Objects.equals(value, nickname.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

}
