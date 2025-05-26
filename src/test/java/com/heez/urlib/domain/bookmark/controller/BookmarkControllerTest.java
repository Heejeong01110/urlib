package com.heez.urlib.domain.bookmark.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkDetailResponse;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkUpdateRequest;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import com.heez.urlib.domain.link.controller.dto.LinkCreateResponse;
import com.heez.urlib.domain.link.controller.dto.LinkDetailResponse;
import com.heez.urlib.domain.link.model.Link;
import java.time.LocalDateTime;
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

  @Test
  @WithMockCustomUser(memberId = 42L)
  void generateBookmark_success() throws Exception {
    // given
    Long memberId = 42L;
    List<String> tags = List.of("spring", "java");
    BaseLinkRequest linkReq = new BaseLinkRequest("http://example.com", "Example");
    BookmarkCreateRequest req = new BookmarkCreateRequest(
        "My Bookmark",
        "A description",
        "http://img.url",
        true,
        tags,
        List.of(linkReq)
    );

    List<LinkCreateResponse> linkRes = List.of(LinkCreateResponse.from(
        new Link(1L, linkReq.url(), linkReq.title(), null)
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
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/v1/bookmarks/" + resp.id()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(resp.id()))
        .andExpect(jsonPath("$.title").value(resp.title()))
        .andExpect(jsonPath("$.description").value(resp.description()))
        .andExpect(jsonPath("$.imageUrl").value(resp.imageUrl()))
        .andExpect(jsonPath("$.visibleToOthers").value(resp.visibleToOthers()))
        .andExpect(jsonPath("$.tags[0]").value("spring"))
        .andExpect(jsonPath("$.links[0].url").value("http://example.com"));

    then(bookmarkService).should().createBookmark(memberId, req);
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void getBookmark_with_visibleToOthers_success() throws Exception {
    // given
    Long memberId = 42L;
    Long bookmarkId = 7L;
    Long writerId = 99L;
    LocalDateTime createdAt = LocalDateTime.of(2025, 5, 23, 15, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2025, 5, 23, 15, 0);
    Bookmark bookmark = Bookmark.builder()
        .bookmarkId(bookmarkId)
        .title("My Bookmark")
        .description("A detailed description")
        .imageUrl("https://example.com/image.png")
        .visibleToOthers(true)
        .viewCount(42L)
        .build();
    List<String> tags = List.of("spring", "java");
    List<LinkDetailResponse> links = List.of(
        new LinkDetailResponse(100L, "Google", "https://google.com"),
        new LinkDetailResponse(101L, "GitHub", "https://github.com")
    );
    BookmarkDetailResponse resp = new BookmarkDetailResponse(
        bookmarkId,
        "My Bookmark",
        "A detailed description",
        "https://example.com/image.png",
        true,
        42L,
        createdAt,
        updatedAt,
        tags,
        links,
        writerId
    );

    given(bookmarkService.getBookmark(memberId, bookmark.getBookmarkId())).willReturn(resp);

    // when & then
    mockMvc.perform(
            get("/api/v1/bookmarks/{bookmarkId}", bookmarkId)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(bookmarkId))
        .andExpect(jsonPath("$.title").value("My Bookmark"))
        .andExpect(jsonPath("$.description").value("A detailed description"))
        .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.png"))
        .andExpect(jsonPath("$.visibleToOthers").value(true))
        .andExpect(jsonPath("$.viewCount").value(42))
        .andExpect(jsonPath("$.createdAt").value("2025-05-23T15:00:00"))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags[0]").value("spring"))
        .andExpect(jsonPath("$.tags[1]").value("java"))
        .andExpect(jsonPath("$.links").isArray())
        .andExpect(jsonPath("$.links.length()").value(2))
        .andExpect(jsonPath("$.links[0].id").value(100))
        .andExpect(jsonPath("$.links[0].title").value("Google"))
        .andExpect(jsonPath("$.links[0].url").value("https://google.com"))
        .andExpect(jsonPath("$.links[1].id").value(101))
        .andExpect(jsonPath("$.links[1].title").value("GitHub"))
        .andExpect(jsonPath("$.links[1].url").value("https://github.com"))
        .andExpect(jsonPath("$.writerId").value(writerId));

    then(bookmarkService).should().getBookmark(memberId, bookmarkId);
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void updateBookmark_success() throws Exception {
    // given
    Long memberId = 42L;
    Long bookmarkId = 7L;
    Long writerId = 99L;
    LocalDateTime createdAt = LocalDateTime.of(2025, 5, 23, 15, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2025, 5, 26, 15, 0);
    List<String> tags = List.of("spring", "java");
    BaseLinkRequest linkReq = new BaseLinkRequest("Example", "http://example.com");
    BookmarkUpdateRequest req = new BookmarkUpdateRequest(
        "My Bookmark",
        "A description",
        "http://img.url",
        true,
        tags,
        List.of(linkReq)
    );

    List<LinkDetailResponse> linkRes = List.of(LinkDetailResponse.from(
        new Link(1L, linkReq.title(), linkReq.url(), null)
    ));
    BookmarkDetailResponse resp = new BookmarkDetailResponse(
        bookmarkId,
        req.title(),
        req.description(),
        req.imageUrl(),
        req.visibleToOthers(),
        10L,
        createdAt,
        updatedAt,
        req.tags(),
        linkRes,
        writerId
    );
    given(bookmarkService.updateBookmark(memberId, bookmarkId, req)).willReturn(resp);

    // when & then
    mockMvc.perform(
            put("/api/v1/bookmarks/{bookmarkId}", bookmarkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(resp.id()))
        .andExpect(jsonPath("$.title").value(resp.title()))
        .andExpect(jsonPath("$.description").value(resp.description()))
        .andExpect(jsonPath("$.imageUrl").value(resp.imageUrl()))
        .andExpect(jsonPath("$.visibleToOthers").value(resp.visibleToOthers()))
        .andExpect(jsonPath("$.tags[0]").value("spring"))
        .andExpect(jsonPath("$.links[0].url").value("http://example.com"))
        .andExpect(jsonPath("$.links[0].title").value("Example"));

    then(bookmarkService).should().updateBookmark(memberId, bookmarkId, req);
  }
}
