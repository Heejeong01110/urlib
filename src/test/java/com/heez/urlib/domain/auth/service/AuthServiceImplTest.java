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
import com.heez.urlib.domain.auth.service.dto.ReissueDto;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;
import com.heez.urlib.domain.member.repository.MemberRepository;
import com.heez.urlib.domain.member.repository.dto.TokenEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  private final String oldRefreshToken = "old-refresh";
  private final Long memberId = 42L;
  @Mock
  private RedisService redisService;
  @Mock
  private AuthTokenProvider authTokenProvider;
  @Mock
  private MemberRepository memberRepository;
  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void reissue_success() {
    // given
    given(redisService.getValue(oldRefreshToken))
        .willReturn(Optional.of(memberId.toString()));

    TokenEntity tokenEntity = mock(TokenEntity.class);
    Email email = new Email("user@example.com");
    given(tokenEntity.email()).willReturn(email);
    given(tokenEntity.role()).willReturn(Role.USER);
    given(memberRepository.findEmailAndRoleById(memberId))
        .willReturn(Optional.of(tokenEntity));

    String newAccess = "new-access-token";
    given(authTokenProvider.generateAccessToken(
        eq(memberId),
        eq(email.getValue()),
        any(List.class)))
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
    given(redisService.getValue(oldRefreshToken))
        .willReturn(Optional.empty());

    assertThrows(InvalidRefreshTokenException.class,
        () -> authService.reissue(oldRefreshToken));

    then(redisService).should(never()).saveToken(anyString(), anyLong());
  }

  @Test
  void reissue_memberNotFound_throws() {
    given(redisService.getValue(oldRefreshToken))
        .willReturn(Optional.of(memberId.toString()));
    given(memberRepository.findEmailAndRoleById(memberId))
        .willReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class,
        () -> authService.reissue(oldRefreshToken));

    then(authTokenProvider).should(never()).generateAccessToken(anyLong(), anyString(), any());
  }

  @Test
  void logout_deletesToken() {
    // when
    authService.logout(oldRefreshToken);

    // then
    then(redisService).should().delete(oldRefreshToken);
  }
}

