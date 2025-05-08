package com.heez.urlib.domain.auth.service;

import com.heez.urlib.domain.auth.exception.InvalidRefreshTokenException;
import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.service.dto.ReissueDto;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.repository.MemberRepository;
import com.heez.urlib.domain.member.repository.dto.TokenEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final RedisService redis;
  private final AuthTokenProvider authTokenProvider;
  private final MemberRepository memberRepository;

  public ReissueDto reissue(String oldRefreshToken) {
    authTokenProvider.validateRefreshToken(oldRefreshToken);
    Long memberId = Long.valueOf(redis.getValue(oldRefreshToken)
        .orElseThrow(InvalidRefreshTokenException::new));
    TokenEntity info = memberRepository.findEmailAndRoleById(memberId)
        .orElseThrow(MemberNotFoundException::new);

    String accessToken = authTokenProvider.generateAccessToken(
        memberId,
        info.email().getValue(),
        List.of(new SimpleGrantedAuthority(info.role().name())));
    String newRefreshToken = authTokenProvider.generateRefreshToken(memberId);

    redis.saveToken(newRefreshToken, memberId);
    redis.delete(oldRefreshToken);
    return new ReissueDto(accessToken, newRefreshToken);
  }
}
