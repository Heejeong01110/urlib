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
    List<String> unique = tags.stream()
        .distinct()
        .toList();

    List<Hashtag> existing = tagRepository.findAllByTitleIn(unique);
    Set<String> existingNames = existing.stream()
        .map(Hashtag::getTitle)
        .collect(Collectors.toSet());

    List<Hashtag> toCreate = unique.stream()
        .filter(name -> !existingNames.contains(name))
        .map(name -> Hashtag.builder()
            .title(name)
            .build())
        .toList();

    List<Hashtag> saved = toCreate.isEmpty()
        ? Collections.emptyList()
        : tagRepository.saveAll(toCreate);

    Map<String, Hashtag> allByName = Stream.concat(
        existing.stream(),
        saved.stream()).collect(Collectors.toMap(Hashtag::getTitle, Function.identity()));

    return unique.stream()
        .map(allByName::get)
        .toList();
  }

  @Override
  public List<String> getTagTitlesByBookmarkId(Long bookmarkId) {
    return bookmarkHashtagRepository.findTagNamesByBookmarkId(bookmarkId);
  }
}
