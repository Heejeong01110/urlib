package com.heez.urlib.domain.auth.service;

import com.heez.urlib.domain.auth.exception.InvalidRefreshTokenException;
import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.auth.security.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.service.dto.ReissueDto;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.repository.MemberRepository;
import com.heez.urlib.domain.member.service.dto.TokenProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final RedisService redisService;
  private final AuthTokenProvider authTokenProvider;
  private final MemberRepository memberRepository;

  @Transactional
  public ReissueDto reissue(String oldRefreshToken) {
    authTokenProvider.validateRefreshToken(oldRefreshToken);
    Long memberId = Long.valueOf(redisService.getValue(oldRefreshToken)
        .orElseThrow(InvalidRefreshTokenException::new));
    TokenProjection info = memberRepository.findEmailAndRoleById(memberId)
        .orElseThrow(MemberNotFoundException::new);

    String accessToken = authTokenProvider.generateAccessToken(
        memberId,
        info.getEmail().getValue(),
        List.of(new SimpleGrantedAuthority(info.getRole().name())), AuthType.NONE);
    String newRefreshToken = authTokenProvider.generateRefreshToken(memberId);

    redisService.saveToken(newRefreshToken, memberId);
    redisService.delete(oldRefreshToken);
    return new ReissueDto(accessToken, newRefreshToken);
  }

  @Transactional
  public void logout(String refreshToken) {
    redisService.delete(refreshToken);
  }
}
