package com.heez.urlib.domain.auth.model;

import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "refreshTokens")
public class RefreshToken implements Serializable {

  private Long id;

  @Id
  private String refreshToken;

  @TimeToLive
  private Long ttl;

}
