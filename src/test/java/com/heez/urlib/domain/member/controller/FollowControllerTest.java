package com.heez.urlib.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.model.WithMockCustomUser;
import com.heez.urlib.domain.member.controller.dto.FollowStatusResponse;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import com.heez.urlib.domain.member.service.FollowService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = FollowController.class)
@AutoConfigureMockMvc(addFilters = false)
class FollowControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private FollowService followService;

  @MockitoBean
  private AuthTokenProvider authTokenProvider;

  @Test
  @WithMockCustomUser
  void getFollowStatus_Success() throws Exception {
    // given
    Long targetMemberId = 42L;
    Long authMemberId = 1L;
    boolean isFollowing = true;

    FollowStatusResponse mockResponse = new FollowStatusResponse(isFollowing);
    when(followService.getFollowStatus(eq(targetMemberId), eq(authMemberId)))
        .thenReturn(mockResponse);

    // when / then
    mockMvc.perform(get("/api/v1/users/{memberId}/follow", targetMemberId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.follow").value(isFollowing));
  }

  @Test
  @WithMockCustomUser
  void followOther_Success() throws Exception {
    // given
    Long targetMemberId = 42L;
    Long authMemberId = 1L;

    doNothing().when(followService).follow(eq(targetMemberId), eq(authMemberId));

    // when / then
    mockMvc.perform(post("/api/v1/users/{memberId}/follow", targetMemberId))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockCustomUser
  void unfollowOther_Success() throws Exception {
    // given
    Long targetMemberId = 42L;
    Long authMemberId = 1L;

    doNothing().when(followService).unfollow(eq(targetMemberId), eq(authMemberId));

    // when / then
    mockMvc.perform(delete("/api/v1/users/{memberId}/unfollow", targetMemberId))
        .andExpect(status().isNoContent());
  }

  @Test
  void getFollowingList_Success() throws Exception {
    //given
    Long memberId = 42L;
    int page = 0;
    int size = 10;

    List<MemberSummaryResponse> content = List.of(
        new MemberSummaryResponse(1L, "https://img.example.com/alice.png"),
        new MemberSummaryResponse(2L, "https://img.example.com/bob.png"));
    Page<MemberSummaryResponse> mockPage =
        new PageImpl<>(content, PageRequest.of(page, size), content.size());

    when(followService.getFollowingList(eq(memberId), any(Pageable.class)))
        .thenReturn(mockPage);

    // when / then
    mockMvc.perform(get("/api/v1/users/{memberId}/following", memberId)
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].memberId").value(1))
        .andExpect(
            jsonPath("$.content[0].memberImageUrl").value("https://img.example.com/alice.png"))
        .andExpect(jsonPath("$.content[1].memberId").value(2))
        .andExpect(
            jsonPath("$.content[1].memberImageUrl").value("https://img.example.com/bob.png"));
  }

  @Test
  void getFollowerList_Success() throws Exception {
    //given
    Long memberId = 99L;
    int page = 1;
    int size = 5;

    List<MemberSummaryResponse> content = List.of(
        new MemberSummaryResponse(10L, "https://img.example.com/charlie.png"));
    Page<MemberSummaryResponse> mockPage =
        new PageImpl<>(content, PageRequest.of(page, size), content.size());

    when(followService.getFollowerList(eq(memberId), any(Pageable.class)))
        .thenReturn(mockPage);

    // when / then
    mockMvc.perform(get("/api/v1/users/{memberId}/follower", memberId)
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].memberId").value(10))
        .andExpect(
            jsonPath("$.content[0].memberImageUrl").value("https://img.example.com/charlie.png"));
  }

  @Test
  @WithMockCustomUser
  void getMyFollowingList_Success() throws Exception {
    // given
    Long memberId = 1L;
    int page = 0;
    int size = 10;

    List<MemberSummaryResponse> content = List.of(
        new MemberSummaryResponse(1L, "https://img.example.com/alice.png"),
        new MemberSummaryResponse(2L, "https://img.example.com/bob.png"));

    Page<MemberSummaryResponse> mockPage = new PageImpl<>(
        content, PageRequest.of(page, size), content.size());

    when(followService.getFollowingList(eq(memberId), any(Pageable.class)))
        .thenReturn(mockPage);

    // when / then
    mockMvc.perform(
            get("/api/v1/users/me/following")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].memberId").value(1))
        .andExpect(jsonPath("$.content[0].memberImageUrl")
            .value("https://img.example.com/alice.png"))
        .andExpect(jsonPath("$.content[1].memberId").value(2))
        .andExpect(jsonPath("$.content[1].memberImageUrl")
            .value("https://img.example.com/bob.png"));
  }

  @Test
  @WithMockCustomUser
  void getMyFollowerList_Success() throws Exception {
    // given
    Long memberId = 1L;
    int page = 1;
    int size = 5;

    List<MemberSummaryResponse> content = List.of(
        new MemberSummaryResponse(10L, "https://img.example.com/charlie.png"));

    Page<MemberSummaryResponse> mockPage = new PageImpl<>(
        content, PageRequest.of(page, size), content.size());

    when(followService.getFollowerList(eq(memberId), any(Pageable.class)))
        .thenReturn(mockPage);

    // when / then
    mockMvc.perform(
            get("/api/v1/users/me/follower")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].memberId").value(10))
        .andExpect(jsonPath("$.content[0].memberImageUrl")
            .value("https://img.example.com/charlie.png"));
  }


}
