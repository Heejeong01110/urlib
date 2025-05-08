package com.heez.urlib.domain.auth.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

  private final RedisTemplate<String, String> redis;
  private final Long refreshTtl;

  public RedisService(
      RedisTemplate<String, String> redis,
      @Value("${spring.security.jwt.refresh-token-expiry}") Long refreshTtl) {
    this.redis = redis;
    this.refreshTtl = refreshTtl;
  }

  public void saveToken(String key, Long value) {
    redis.opsForValue().set(key, value.toString(), refreshTtl, TimeUnit.SECONDS);
  }

  public void delete(String key) {
    redis.delete(key);
  }

  public Optional<String> getValue(String key) {
    return Optional.ofNullable(redis.opsForValue().get(key)).map(String::valueOf);
  }

}
