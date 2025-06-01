package com.heez.urlib.domain.member.controller;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.model.WithMockCustomUser;
import com.heez.urlib.domain.member.controller.dto.MemberDetailResponse;
import com.heez.urlib.domain.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MemberService memberService;

  @MockitoBean
  private AuthTokenProvider authTokenProvider;

  @Test
  @WithMockCustomUser
  void getMyProfile_success() throws Exception {
    // given
    MemberDetailResponse detail = new MemberDetailResponse(
        1L,
        "http://example.com/me.png",
        "나의 프로필 설명"
    );
    given(memberService.getProfile(1L)).willReturn(detail);

    // when / then
    mockMvc.perform(get("/api/v1/users/me")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.memberId").value(1))
        .andExpect(jsonPath("$.imageUrl").value("http://example.com/me.png"))
        .andExpect(jsonPath("$.description").value("나의 프로필 설명"));
  }

  @Test
  void getProfile_success() throws Exception {
    // given
    long memberId = 42L;
    MemberDetailResponse detail = new MemberDetailResponse(
        memberId,
        "http://example.com/other.png",
        "다른 사용자 설명"
    );
    given(memberService.getProfile(memberId)).willReturn(detail);

    // when / then
    mockMvc.perform(get("/api/v1/users/{memberId}", memberId)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.memberId").value(memberId))
        .andExpect(jsonPath("$.imageUrl").value("http://example.com/other.png"))
        .andExpect(jsonPath("$.description").value("다른 사용자 설명"));
  }
}
