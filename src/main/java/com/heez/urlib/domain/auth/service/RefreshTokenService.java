package com.heez.urlib.domain.auth.service;

import com.heez.urlib.domain.auth.exception.TokenValidFailedException;
import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.model.RefreshToken;
import com.heez.urlib.domain.auth.repository.RefreshTokenRepository;
import com.heez.urlib.global.error.response.ErrorCode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

  private final Long refreshTtl;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AuthTokenProvider authTokenProvider;

  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository,
      AuthTokenProvider authTokenProvider,
      @Value("${spring.security.jwt.refresh-token-expiry}") Long refreshTtl
  ) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.authTokenProvider = authTokenProvider;
    this.refreshTtl = refreshTtl;
  }

  @Transactional
  public void saveToken(Long userId, String refreshToken) {
    refreshTokenRepository.save(
        new RefreshToken(userId, refreshToken, refreshTtl));
  }

  @Transactional
  public RefreshToken reIssueAccessToken(String refreshToken) {
    RefreshToken findToken = refreshTokenRepository.findById(refreshToken)
        .orElseThrow(() -> new TokenValidFailedException(ErrorCode.EXPIRED_TOKEN));

    return refreshTokenRepository.save(RefreshToken.builder()
        .id(findToken.getId())
        .refreshToken(authTokenProvider.generateRefreshToken(findToken.getId()))
        .build());
  }


}
