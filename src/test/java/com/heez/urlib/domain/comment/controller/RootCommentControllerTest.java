package com.heez.urlib.domain.comment.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.model.WithMockCustomUser;
import com.heez.urlib.domain.auth.security.jwt.AuthTokenProvider;
import com.heez.urlib.domain.comment.controller.dto.CommentCreateRequest;
import com.heez.urlib.domain.comment.controller.dto.CommentDetailResponse;
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

@WebMvcTest(controllers = RootCommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class RootCommentControllerTest {

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
  void getComments_success() throws Exception {
    // given
    Long memberId = 42L;
    Long bookmarkId = 77L;

    CommentDetailResponse comment1 = new CommentDetailResponse(101L, "댓글1",
        new MemberSummaryResponse(1L, "http://image.url/1"));
    CommentDetailResponse comment2 = new CommentDetailResponse(102L, "댓글2",
        new MemberSummaryResponse(2L, "http://image.url/2"));
    List<CommentDetailResponse> comments = List.of(comment1, comment2);
    Page<CommentDetailResponse> page = new PageImpl<>(comments);

    given(commentService.getParentComments(
        Optional.of(memberId), bookmarkId, PageRequest.of(0, 10, Sort.by("createdAt").descending()))
    ).willReturn(page);

    // when & then
    mockMvc.perform(
            get("/api/v1/bookmarks/{bookmarkId}/comments", bookmarkId)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].id").value(101L))
        .andExpect(jsonPath("$.content[0].content").value("댓글1"))
        .andExpect(jsonPath("$.content[0].memberInfo.memberId").value(1L))
        .andExpect(jsonPath("$.content[0].memberInfo.memberImageUrl").value("http://image.url/1"))
        .andExpect(jsonPath("$.content[1].id").value(102L))
        .andExpect(jsonPath("$.content[1].content").value("댓글2"))
        .andExpect(jsonPath("$.content[1].memberInfo.memberId").value(2L))
        .andExpect(jsonPath("$.content[1].memberInfo.memberImageUrl").value("http://image.url/2"));

    then(commentService).should().getParentComments(
        Optional.of(memberId), bookmarkId,
        PageRequest.of(0, 10, Sort.by("createdAt").descending()));
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void createComment_success() throws Exception {
    // given
    Long memberId = 42L;
    Long bookmarkId = 77L;

    CommentCreateRequest request = CommentCreateRequest.builder()
        .content("새 댓글입니다")
        .parentCommentId(null)
        .build();

    MemberSummaryResponse memberInfo = new MemberSummaryResponse(memberId, "http://image.url/me");

    CommentDetailResponse response = CommentDetailResponse.builder()
        .id(300L)
        .content("새 댓글입니다")
        .memberInfo(memberInfo)
        .build();

    given(commentService.createComment(bookmarkId, memberId, request)).willReturn(response);

    // when & then
    mockMvc.perform(
            post("/api/v1/bookmarks/{bookmarkId}/comments", bookmarkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location",
            "http://localhost/api/v1/bookmarks/" + bookmarkId + "/comments/" + response.id()))
        .andExpect(jsonPath("$.id").value(300L))
        .andExpect(jsonPath("$.content").value("새 댓글입니다"))
        .andExpect(jsonPath("$.memberInfo.memberId").value(memberId))
        .andExpect(jsonPath("$.memberInfo.memberImageUrl").value("http://image.url/me"));

    then(commentService).should().createComment(bookmarkId, memberId, request);
  }


}
