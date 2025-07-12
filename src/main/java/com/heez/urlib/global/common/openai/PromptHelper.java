package com.heez.urlib.global.common.openai;

import java.util.List;

public class PromptHelper {

  public static String generateBookmarkTitlePrompt(List<String> titles) {
    return "다음은 사용자가 저장한 링크 제목 목록입니다.\n" +
        String.join("\n", titles.stream().map(t -> "- " + t).toList()) +
        "\n\n위 제목들을 바탕으로 이 북마크의 대표 제목을 추천해주세요.\n" +
        "출력 예시:\n" +
        "제목: ~~~ ";
  }
}
