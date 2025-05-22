package com.heez.urlib.domain.bookmark.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.jwt.AuthTokenProvider;
import com.heez.urlib.domain.auth.model.WithMockCustomUser;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkCreateResponse;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.domain.link.controller.dto.LinkCreateRequest;
import com.heez.urlib.domain.link.controller.dto.LinkCreateResponse;
import com.heez.urlib.domain.link.model.Link;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BookmarkController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookmarkControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BookmarkService bookmarkService;

  @MockitoBean
  private AuthTokenProvider authTokenProvider;

  @WithMockCustomUser(memberId = 42L)
  @Test
  void generateBookmark_success() throws Exception {
    // given
    Long memberId = 42L;
    List<String> tags = List.of("spring", "java");
    LinkCreateRequest linkReq = new LinkCreateRequest("http://example.com", "Example");
    BookmarkCreateRequest req = new BookmarkCreateRequest(
        "http://img.url",
        "My Bookmark",
        tags,
        "A description",
        true,
        List.of(linkReq)
    );

    List<LinkCreateResponse> linkRes = List.of(LinkCreateResponse.from(
        new Link(null, linkReq.url(), linkReq.title(), null)
    ));
    BookmarkCreateResponse resp = new BookmarkCreateResponse(
        100L,
        req.title(),
        req.description(),
        req.imageUrl(),
        req.visibleToOthers(),
        req.tags(),
        linkRes
    );
    given(bookmarkService.createBookmark(memberId, req)).willReturn(resp);

    // when & then
    mockMvc.perform(
            post("/api/v1/bookmarks/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/v1/bookmarks/" + resp.id()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(resp.id()))
        .andExpect(jsonPath("$.name").value(resp.name()))
        .andExpect(jsonPath("$.description").value(resp.description()))
        .andExpect(jsonPath("$.imageUrl").value(resp.imageUrl()))
        .andExpect(jsonPath("$.visibleToOthers").value(resp.visibleToOthers()))
        .andExpect(jsonPath("$.tags[0]").value("spring"))
        .andExpect(jsonPath("$.links[0].url").value("http://example.com"));

    then(bookmarkService).should().createBookmark(memberId, req);
  }
}
