package com.heez.urlib.domain.comment.controller;


import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.model.WithMockCustomUser;
import com.heez.urlib.domain.auth.security.jwt.AuthTokenProvider;
import com.heez.urlib.domain.comment.controller.dto.CommentCreateRequest;
import com.heez.urlib.domain.comment.controller.dto.CommentDetailResponse;
import com.heez.urlib.domain.comment.controller.dto.CommentUpdateRequest;
import com.heez.urlib.domain.comment.service.CommentService;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CommentService commentService;

  @MockitoBean
  private AuthTokenProvider authTokenProvider;

  @Test
  @WithMockCustomUser(memberId = 42L)
  void getChildrenComments_success() throws Exception {
    // given
    Long memberId = 42L;
    Long parentCommentId = 10L;

    CommentDetailResponse reply1 = new CommentDetailResponse(101L, "첫 번째 댓글",
        new MemberSummaryResponse(1L, "http://image.url"));
    CommentDetailResponse reply2 = new CommentDetailResponse(102L, "두 번째 댓글",
        new MemberSummaryResponse(2L, "http://image.url2"));

    List<CommentDetailResponse> replies = List.of(reply1, reply2);
    Page<CommentDetailResponse> page = new PageImpl<>(replies);

    given(commentService.getChildrenComments(
        Optional.of(memberId), parentCommentId,
        PageRequest.of(0, 10, Sort.by("createdAt").descending())))
        .willReturn(page);

    // when & then
    mockMvc.perform(
            get("/api/v1/comments/{commentId}/replies", parentCommentId)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.length()").value(2))

        .andExpect(jsonPath("$.content[0].id").value(101L))
        .andExpect(jsonPath("$.content[0].content").value("첫 번째 댓글"))
        .andExpect(jsonPath("$.content[0].memberInfo.memberId").value(1L))
        .andExpect(jsonPath("$.content[0].memberInfo.memberImageUrl").value("http://image.url"))

        .andExpect(jsonPath("$.content[1].id").value(102L))
        .andExpect(jsonPath("$.content[1].content").value("두 번째 댓글"))
        .andExpect(jsonPath("$.content[1].memberInfo.memberId").value(2L))
        .andExpect(jsonPath("$.content[1].memberInfo.memberImageUrl").value("http://image.url2"));

    then(commentService).should()
        .getChildrenComments(Optional.of(memberId), parentCommentId,
            PageRequest.of(0, 10, Sort.by("createdAt").descending()));
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void createChildrenComment_success() throws Exception {
    // given
    Long memberId = 42L;
    Long parentCommentId = 100L;
    String content = "대댓글입니다.";

    CommentCreateRequest request = CommentCreateRequest.builder()
        .content(content)
        .parentCommentId(parentCommentId)
        .build();

    MemberSummaryResponse memberSummary = new MemberSummaryResponse(memberId,
        "https://image.url/profile.png");

    CommentDetailResponse response = CommentDetailResponse.builder()
        .id(200L)
        .content(content)
        .memberInfo(memberSummary)
        .build();

    given(commentService.createReplyComment(parentCommentId, memberId, request)).willReturn(
        response);

    // when & then
    mockMvc.perform(
            post("/api/v1/comments/{commentId}/replies", parentCommentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location",
            "http://localhost/api/v1/comments/" + response.id()))
        .andExpect(jsonPath("$.id").value(response.id()))
        .andExpect(jsonPath("$.content").value(content))
        .andExpect(jsonPath("$.memberInfo.memberId").value(memberId))
        .andExpect(jsonPath("$.memberInfo.memberImageUrl").value(memberSummary.memberImageUrl()));

    then(commentService).should().createReplyComment(parentCommentId, memberId, request);
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void updateComment_success() throws Exception {
    // given
    Long memberId = 42L;
    Long commentId = 123L;

    CommentUpdateRequest request = CommentUpdateRequest.builder()
        .content("수정된 댓글입니다.")
        .build();

    MemberSummaryResponse memberInfo = new MemberSummaryResponse(memberId,
        "http://image.url/profile.png");

    CommentDetailResponse response = CommentDetailResponse.builder()
        .id(commentId)
        .content(request.content())
        .memberInfo(memberInfo)
        .build();

    given(commentService.updateComment(memberId, commentId, request)).willReturn(response);

    // when & then
    mockMvc.perform(
            put("/api/v1/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(commentId))
        .andExpect(jsonPath("$.content").value(request.content()))
        .andExpect(jsonPath("$.memberInfo.memberId").value(memberId))
        .andExpect(jsonPath("$.memberInfo.memberImageUrl").value(memberInfo.memberImageUrl()));

    then(commentService).should().updateComment(memberId, commentId, request);
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void deleteComment_success() throws Exception {
    // given
    Long memberId = 42L;
    Long commentId = 123L;

    willDoNothing().given(commentService).deleteComment(memberId, commentId);

    // when & then
    mockMvc.perform(
            delete("/api/v1/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    then(commentService).should().deleteComment(memberId, commentId);
  }

}
