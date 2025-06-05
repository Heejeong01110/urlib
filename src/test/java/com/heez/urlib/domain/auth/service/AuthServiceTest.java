package com.heez.urlib.domain.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.heez.urlib.domain.auth.exception.InvalidRefreshTokenException;
import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.model.AuthType;
import com.heez.urlib.domain.auth.service.dto.ReissueDto;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.repository.MemberRepository;
import com.heez.urlib.domain.member.service.dto.TokenProjection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private RedisService redisService;
  @Mock
  private AuthTokenProvider authTokenProvider;
  @Mock
  private MemberRepository memberRepository;
  @InjectMocks
  private AuthService authService;

  @Test
  void reissue_success() {
    // given
    String oldRefreshToken = "old-refresh";
    Long memberId = 42L;
    given(redisService.getValue(oldRefreshToken))
        .willReturn(Optional.of(memberId.toString()));

    TokenProjection tokenProjection = mock(TokenProjection.class);
    Email email = new Email("user@example.com");
    given(tokenProjection.getEmail()).willReturn(email);
    given(tokenProjection.getRole()).willReturn(Role.USER);
    given(memberRepository.findEmailAndRoleById(memberId))
        .willReturn(Optional.of(tokenProjection));

    String newAccess = "new-access-token";
    given(authTokenProvider.generateAccessToken(
        eq(memberId),
        eq(email.getValue()),
        any(List.class),
        eq(AuthType.NONE)))
        .willReturn(newAccess);

    String newRefresh = "new-refresh-token";
    given(authTokenProvider.generateRefreshToken(memberId))
        .willReturn(newRefresh);

    // when
    ReissueDto dto = authService.reissue(oldRefreshToken);

    // then
    assertEquals(newAccess, dto.accessToken());
    assertEquals(newRefresh, dto.refreshToken());

    then(redisService).should().saveToken(newRefresh, memberId);
    then(redisService).should().delete(oldRefreshToken);
  }

  @Test
  void reissue_invalidRefreshToken_throws() {
    // given
    String oldRefreshToken = "old-refresh";
    given(redisService.getValue(oldRefreshToken))
        .willReturn(Optional.empty());

    // when / then
    assertThrows(InvalidRefreshTokenException.class,
        () -> authService.reissue(oldRefreshToken));

    then(redisService).should(never()).saveToken(anyString(), anyLong());
  }

  @Test
  void reissue_memberNotFound_throws() {
    // given
    String oldRefreshToken = "old-refresh";
    Long memberId = 42L;
    given(redisService.getValue(oldRefreshToken))
        .willReturn(Optional.of(memberId.toString()));
    given(memberRepository.findEmailAndRoleById(memberId))
        .willReturn(Optional.empty());

    // when / then
    assertThrows(MemberNotFoundException.class,
        () -> authService.reissue(oldRefreshToken));

    then(authTokenProvider).should(never())
        .generateAccessToken(anyLong(), anyString(), any(), any());
  }

  @Test
  void logout_deletesToken() {
    // given
    String oldRefreshToken = "old-refresh";

    // when
    authService.logout(oldRefreshToken);

    // then
    then(redisService).should().delete(oldRefreshToken);
  }
}

