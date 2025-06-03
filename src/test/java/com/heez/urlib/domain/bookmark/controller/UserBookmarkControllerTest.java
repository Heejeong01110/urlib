package com.heez.urlib.domain.bookmark.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.model.WithMockCustomUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkSummaryResponse;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserBookmarkController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserBookmarkControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BookmarkService bookmarkService;

  @MockitoBean
  private AuthTokenProvider authTokenProvider;

  @Test
  @WithMockCustomUser
  void getMyBookmarks_success() throws Exception {
    // given
    BookmarkSummaryResponse resp = new BookmarkSummaryResponse(
        100L,
        "테스트 제목",
        "테스트 설명",
        "http://example.com/image.png",
        new MemberSummaryResponse(1L, "http://example.com/member.png")
    );
    Page<BookmarkSummaryResponse> page = new PageImpl<>(
        List.of(resp),
        PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt"),
        1
    );

    given(bookmarkService.getBookmarkSummaryListByMemberId(eq(1L), eq(1L), any(Pageable.class)))
        .willReturn(page);

    // when / then
    mockMvc.perform(get("/api/v1/users/me/bookmarks")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "createdAt,desc")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(100))
        .andExpect(jsonPath("$.content[0].title").value("테스트 제목"))
        .andExpect(jsonPath("$.content[0].description").value("테스트 설명"))
        .andExpect(jsonPath("$.content[0].bookmarkImageUrl").value("http://example.com/image.png"))
        .andExpect(jsonPath("$.content[0].memberSummary.memberId").value(1))
        .andExpect(
            jsonPath("$.content[0].memberSummary.memberImageUrl").value(
                "http://example.com/member.png"));
  }

  @Test
  @WithMockCustomUser
  void getBookmarks_success() throws Exception {
    // given
    long viewerId = 1L;
    long ownerId = 42L;

    BookmarkSummaryResponse resp = new BookmarkSummaryResponse(
        200L,
        "다른 유저 북마크",
        "설명",
        "http://example.com/other.png",
        new MemberSummaryResponse(ownerId, "http://example.com/owner.png")
    );
    Page<BookmarkSummaryResponse> page = new PageImpl<>(
        List.of(resp),
        PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt"),
        1
    );

    given(bookmarkService.getBookmarkSummaryListByMemberId(eq(viewerId), eq(ownerId),
        any(Pageable.class)))
        .willReturn(page);

    // when / then
    mockMvc.perform(get("/api/v1/users/{memberId}/bookmarks", ownerId)
            .param("page", "0")
            .param("size", "10")
            .param("sort", "createdAt,desc")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(200))
        .andExpect(jsonPath("$.content[0].title").value("다른 유저 북마크"))
        .andExpect(jsonPath("$.content[0].description").value("설명"))
        .andExpect(jsonPath("$.content[0].bookmarkImageUrl").value("http://example.com/other.png"))
        .andExpect(jsonPath("$.content[0].memberSummary.memberId").value(ownerId))
        .andExpect(
            jsonPath("$.content[0].memberSummary.memberImageUrl").value(
                "http://example.com/owner.png"));
  }
}
