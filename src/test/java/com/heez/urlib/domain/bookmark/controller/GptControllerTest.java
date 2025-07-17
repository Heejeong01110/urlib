package com.heez.urlib.domain.bookmark.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heez.urlib.domain.auth.model.WithMockCustomUser;
import com.heez.urlib.domain.auth.security.jwt.AuthTokenProvider;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkTitleSuggestRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkTitleSuggestResponse;
import com.heez.urlib.domain.bookmark.exception.GptResponseParsingException;
import com.heez.urlib.domain.bookmark.service.GptService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = GptController.class)
@AutoConfigureMockMvc(addFilters = false)
class GptControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private GptService gptService;

  @MockitoBean
  private AuthTokenProvider authTokenProvider;


  @Test
  @WithMockCustomUser(memberId = 42L)
  void generateBookmark_success() throws Exception {
    // given
    BookmarkTitleSuggestRequest request = new BookmarkTitleSuggestRequest(
        List.of("자바", "스프링", "테스트 제목"));
    BookmarkTitleSuggestResponse response = BookmarkTitleSuggestResponse.builder()
        .recommend("추천된 제목입니다.")
        .build();

    given(gptService.getSuggestion(any())).willReturn(response);

    // when & then
    mockMvc.perform(post("/api/v1/ai/suggestions/bookmarks/title")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.recommend").value("추천된 제목입니다."));

    then(gptService).should().getSuggestion(any());
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void titleSuggest_whenRequestIsInvalid_returnsBadRequest() throws Exception {
    BookmarkTitleSuggestRequest invalidRequest = new BookmarkTitleSuggestRequest(List.of());

    // when & then
    mockMvc.perform(post("/api/v1/ai/suggestions/bookmarks/title")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest))
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockCustomUser(memberId = 42L)
  void titleSuggest_whenGptFails_returnsServerError() throws Exception {
    // given
    BookmarkTitleSuggestRequest request = new BookmarkTitleSuggestRequest(List.of("테스트"));

    given(gptService.getSuggestion(any()))
        .willThrow(new GptResponseParsingException());

    // when & then
    mockMvc.perform(post("/api/v1/ai/suggestions/bookmarks/title")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("GPT 응답 중 오류가 발생했습니다."))
        .andExpect(jsonPath("$.code").value("GPT001"));

    then(gptService).should().getSuggestion(any());
  }
}
