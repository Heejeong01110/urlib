package com.heez.urlib.domain.bookmark.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkTitleSuggestRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkTitleSuggestResponse;
import com.heez.urlib.domain.bookmark.exception.GptResponseParsingException;
import com.heez.urlib.global.common.openai.OpenAiProperties;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GptServiceTest {

  @Mock
  private RestTemplate restTemplate;

  private GptService gptService;

  @BeforeEach
  void setUp() {
    OpenAiProperties.Api api = new OpenAiProperties.Api("https://fake-url.com", "fake-key");
    OpenAiProperties properties = new OpenAiProperties("gpt-4", api);

    gptService = new GptService(restTemplate, properties);
  }

  @Test
  void getSuggestion_success() {
    // given
    BookmarkTitleSuggestRequest request = new BookmarkTitleSuggestRequest(List.of("자바", "스프링"));

    Map<String, Object> mockResponse = Map.of(
        "choices", List.of(
            Map.of("message", Map.of("content", "추천 제목 예시입니다."))
        )
    );

    given(restTemplate.postForEntity(
        any(String.class),
        any(HttpEntity.class),
        eq(Map.class)
    )).willReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

    // when
    BookmarkTitleSuggestResponse response = gptService.getSuggestion(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.recommend()).isEqualTo("추천 제목 예시입니다.");
  }

  @Test
  void getSuggestion_whenContentMissing_throwsGptResponseParsingException() {
    // given
    BookmarkTitleSuggestRequest request = new BookmarkTitleSuggestRequest(List.of("테스트"));

    Map<String, Object> malformedResponse = Map.of(
        "choices", List.of(Map.of("message", Map.of()))  // content 누락
    );

    given(restTemplate.postForEntity(
        any(String.class),
        any(HttpEntity.class),
        eq(Map.class)
    )).willReturn(new ResponseEntity<>(malformedResponse, HttpStatus.OK));

    // when & then
    assertThatThrownBy(() -> gptService.getSuggestion(request))
        .isInstanceOf(GptResponseParsingException.class);
  }

  @Test
  void getSuggestion_whenChoicesMissing_throwsGptResponseParsingException() {
    // given
    BookmarkTitleSuggestRequest request = new BookmarkTitleSuggestRequest(List.of("테스트"));

    Map<String, Object> malformedResponse = Map.of();  // choices 자체 누락

    given(restTemplate.postForEntity(
        any(String.class),
        any(HttpEntity.class),
        eq(Map.class)
    )).willReturn(new ResponseEntity<>(malformedResponse, HttpStatus.OK));

    // when & then
    assertThatThrownBy(() -> gptService.getSuggestion(request))
        .isInstanceOf(GptResponseParsingException.class);
  }

  @Test
  void getSuggestion_whenChoicesIsEmpty_throwsException() {
    // given
    BookmarkTitleSuggestRequest request = new BookmarkTitleSuggestRequest(List.of("링크 제목"));
    Map<String, Object> response = Map.of("choices", List.of());

    given(restTemplate.postForEntity(
        any(String.class),
        any(HttpEntity.class),
        eq(Map.class)
    )).willReturn(new ResponseEntity<>(response, HttpStatus.OK));

    // when & then
    assertThatThrownBy(() -> gptService.getSuggestion(request))
        .isInstanceOf(GptResponseParsingException.class);
  }
}
