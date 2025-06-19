package com.heez.urlib.domain.bookmark.controller;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.model.WithMockCustomUser;
import com.heez.urlib.domain.auth.security.jwt.AuthTokenProvider;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkShareRequest;
import com.heez.urlib.domain.bookmark.model.ShareRole;
import com.heez.urlib.domain.bookmark.service.BookmarkPermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BookmarkShareController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookmarkShareControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BookmarkPermissionService bookmarkPermissionService;

  @MockitoBean
  private AuthTokenProvider authTokenProvider;

  @Test
  @WithMockCustomUser(memberId = 42L)
  void updateBookmarkShare_success() throws Exception {
    // given
    Long memberId = 42L;
    Long bookmarkId = 10L;

    BookmarkShareRequest request = new BookmarkShareRequest(
        99L,
        ShareRole.BOOKMARK_VIEWER
    );

    // when & then
    mockMvc.perform(
            put("/api/v1/bookmarks/{bookmarkId}/shares", bookmarkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());

    then(bookmarkPermissionService)
        .should()
        .updateBookmarkShare(bookmarkId, memberId, request);
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void deleteBookmarkShare_success() throws Exception {
    // given
    Long myMemberId = 42L;
    Long bookmarkId = 100L;
    Long targetMemberId = 77L;

    // when & then
    mockMvc.perform(
            delete("/api/v1/bookmarks/{bookmarkId}/shares/{memberId}", bookmarkId, targetMemberId))
        .andExpect(status().isNoContent());

    then(bookmarkPermissionService)
        .should()
        .deleteBookmarkShare(bookmarkId, myMemberId, targetMemberId);
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void leaveBookmarkShare_success() throws Exception {
    // given
    Long memberId = 42L;
    Long bookmarkId = 100L;

    // when & then
    mockMvc.perform(delete("/api/v1/bookmarks/{bookmarkId}/shares/me", bookmarkId))
        .andExpect(status().isNoContent());

    then(bookmarkPermissionService)
        .should()
        .leaveBookmarkShare(bookmarkId, memberId);
  }
}
