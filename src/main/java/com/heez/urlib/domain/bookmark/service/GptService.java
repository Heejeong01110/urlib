package com.heez.urlib.domain.bookmark.service;

import com.heez.urlib.domain.bookmark.controller.dto.BookmarkTitleSuggestRequest;
import com.heez.urlib.domain.bookmark.controller.dto.BookmarkTitleSuggestResponse;
import com.heez.urlib.domain.bookmark.exception.GptResponseParsingException;
import com.heez.urlib.global.common.openai.OpenAiProperties;
import com.heez.urlib.global.common.openai.PromptHelper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

  private final RestTemplate restTemplate;
  private final OpenAiProperties openAiProperties;

  public BookmarkTitleSuggestResponse getSuggestion(BookmarkTitleSuggestRequest request) {
    String prompt = PromptHelper.generateBookmarkTitlePrompt(request.titles());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiProperties.getApi().getKey());

    Map<String, Object> requestBody = Map.of(
        "model", openAiProperties.getModel(),
        "messages", List.of(Map.of("role", "user", "content", prompt)));

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(
        openAiProperties.getApi().getUrl(),
        entity,
        Map.class
    );

    String content = Optional.ofNullable(response.getBody())
        .map(body -> (List<?>) body.get("choices"))
        .filter(choices -> !choices.isEmpty())
        .map(choices -> (Map<?, ?>) choices.get(0))
        .map(choice -> (Map<?, ?>) choice.get("message"))
        .map(message -> message.get("content"))
        .map(Object::toString)
        .orElseThrow(GptResponseParsingException::new);

    return BookmarkTitleSuggestResponse.builder()
        .recommend(content)
        .build();
  }
}
