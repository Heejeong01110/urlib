package com.heez.urlib.domain.tag.service;

import com.heez.urlib.domain.tag.model.Hashtag;
import com.heez.urlib.domain.tag.repository.BookmarkHashtagRepository;
import com.heez.urlib.domain.tag.repository.TagRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;
  private final BookmarkHashtagRepository bookmarkHashtagRepository;

  @Override
  @Transactional
  public List<Hashtag> ensureTags(List<String> tags) {
    List<String> distinctNames = tags.stream()
        .distinct()
        .toList();

    List<Hashtag> existing = tagRepository.findAllByTitleIn(distinctNames);
    Set<String> existingNames = existing.stream()
        .map(Hashtag::getTitle)
        .collect(Collectors.toSet());

    // 없는 이름만 새로 생성
    List<Hashtag> toCreate = distinctNames.stream()
        .filter(name -> !existingNames.contains(name))
        .map(name -> Hashtag.builder()
            .title(name)
            .build())
        .toList();

    // 새 태그 일괄 저장
    List<Hashtag> saved = toCreate.isEmpty()
        ? Collections.emptyList()
        : tagRepository.saveAll(toCreate);

    // 기존 + 새로 저장된 태그
    Map<String, Hashtag> allByName = Stream.concat(existing.stream(), saved.stream())
        .collect(Collectors.toMap(Hashtag::getTitle, Function.identity()));

    return distinctNames.stream()
        .map(allByName::get)
        .toList();
  }

  @Override
  public List<String> getTagTitlesByBookmarkId(Long bookmarkId) {
    return bookmarkHashtagRepository.findTagNamesByBookmarkId(bookmarkId);
  }
}
