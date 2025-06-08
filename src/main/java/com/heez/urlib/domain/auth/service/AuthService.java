package com.heez.urlib.domain.auth.service;

import com.heez.urlib.domain.auth.controller.dto.SignUpRequest;
import com.heez.urlib.domain.auth.exception.DuplicateEmailByEmailTypeException;
import com.heez.urlib.domain.auth.exception.DuplicateEmailByKakaoTypeException;
import com.heez.urlib.domain.auth.exception.DuplicateNicknameException;
import com.heez.urlib.domain.auth.exception.InvalidRefreshTokenException;
import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.auth.security.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.service.dto.ReissueDto;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.model.vo.Nickname;
import com.heez.urlib.domain.member.repository.MemberRepository;
import com.heez.urlib.domain.member.service.dto.TokenProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final RedisService redisService;
  private final AuthTokenProvider authTokenProvider;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

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

  public void signup(SignUpRequest request) {
    memberRepository.findAuthTypeByEmail(new Email(request.email()))
        .ifPresent(this::throwByAuthType);

    if (memberRepository.existsByNickname(new Nickname(request.nickname()))) {
      throw new DuplicateNicknameException();
    }

    memberRepository.save(Member.builder()
        .email(new Email(request.email()))
        .password(passwordEncoder.encode(request.password()))
        .nickname(new Nickname(request.nickname()))
        .role(Role.USER)
        .oauthType(AuthType.EMAIL)
        .build());
  }

  private void throwByAuthType(AuthType authType) {
    switch (authType) {
      case KAKAO -> throw new DuplicateEmailByKakaoTypeException();
      case EMAIL -> throw new DuplicateEmailByEmailTypeException();
      default -> {
      }
    }
  }
}
