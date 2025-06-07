package com.heez.urlib.domain.auth.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.heez.urlib.domain.auth.security.jwt.JwtHeaderUtil;
import com.heez.urlib.domain.auth.service.AuthService;
import com.heez.urlib.domain.auth.service.dto.ReissueDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthController authController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  void reissue_success() throws Exception {
    String oldRefresh = "old-refresh-token";
    ReissueDto dto = new ReissueDto("new-access-token", "new-refresh-token");

    try (MockedStatic<JwtHeaderUtil> jwtUtil = mockStatic(JwtHeaderUtil.class)) {
      jwtUtil.when(() -> JwtHeaderUtil.resolveRefreshToken(any(HttpServletRequest.class)))
          .thenReturn(oldRefresh);
      jwtUtil.when(() -> JwtHeaderUtil.toCookie(eq(dto.refreshToken())))
          .thenReturn("refresh=new-refresh-token; HttpOnly; Path=/");

      given(authService.reissue(oldRefresh)).willReturn(dto);

      mockMvc.perform(post("/api/v1/auth/re-issue"))
          .andExpect(status().isOk())
          .andExpect(header().string(JwtHeaderUtil.HEADER_AUTHORIZATION,
              JwtHeaderUtil.TOKEN_PREFIX + dto.accessToken()
          ))
          .andExpect(header().string(HttpHeaders.SET_COOKIE,
              containsString("refresh=new-refresh-token"))
          )
          .andExpect(header().string(HttpHeaders.SET_COOKIE,
              containsString("HttpOnly"))
          )
          .andExpect(header().string(HttpHeaders.SET_COOKIE,
              containsString("Path=/"))
          )
          .andExpect(content().contentType("application/json;charset=UTF-8"));

      verify(authService).reissue(oldRefresh);
    }
  }

  @Test
  void logout_success() throws Exception {
    String oldRefresh = "old-refresh-token";

    try (MockedStatic<JwtHeaderUtil> jwtUtil = mockStatic(JwtHeaderUtil.class)) {
      jwtUtil.when(() -> JwtHeaderUtil.resolveRefreshToken(any(HttpServletRequest.class)))
          .thenReturn(oldRefresh);
      jwtUtil.when(JwtHeaderUtil::deleteCookie)
          .thenReturn(
              "refresh=; Path=/; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; HttpOnly");

      mockMvc.perform(post("/api/v1/auth/logout"))
          .andExpect(status().isNoContent())
          .andExpect(header().string(
              JwtHeaderUtil.HEADER_AUTHORIZATION,
              ""
          ))
          .andExpect(header().string(HttpHeaders.SET_COOKIE,
              containsString("refresh=;")))
          .andExpect(header().string(HttpHeaders.SET_COOKIE,
              containsString("Max-Age=0")))
          .andExpect(header().string(HttpHeaders.SET_COOKIE,
              containsString("Expires=Thu, 1 Jan 1970")))
          .andExpect(header().string(HttpHeaders.SET_COOKIE,
              containsString("HttpOnly")));

      verify(authService).logout(oldRefresh);
    }
  }
}
