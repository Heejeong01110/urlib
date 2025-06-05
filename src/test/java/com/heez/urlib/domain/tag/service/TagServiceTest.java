package com.heez.urlib.domain.tag.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.heez.urlib.domain.tag.model.Hashtag;
import com.heez.urlib.domain.tag.repository.BookmarkHashtagRepository;
import com.heez.urlib.domain.tag.repository.TagRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock
  private TagRepository tagRepository;

  @Mock
  private BookmarkHashtagRepository bookmarkHashtagRepository;

  @InjectMocks
  private TagService tagService;

  @Test
  void ensureTags_existingNames_returnsExistingEntities() {
    // given
    List<String> names = List.of("spring", "java");
    given(tagRepository.findAllByTitleIn(names))
        .willReturn(List.of(
            Hashtag.builder().hashtagId(1L).title("spring").build(),
            Hashtag.builder().hashtagId(2L).title("java").build()
        ));

    // when
    List<Hashtag> result = tagService.ensureTags(names);

    // then
    assertThat(result)
        .hasSize(2)
        .extracting(Hashtag::getTitle)
        .containsExactlyInAnyOrder("spring", "java");

    then(tagRepository).should().findAllByTitleIn(names);
    then(tagRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  void ensureTags_newNames_createsAndReturnsAll() {
    // given
    List<String> names = List.of("spring", "hibernate");
    given(tagRepository.findAllByTitleIn(names))
        .willReturn(List.of(
            Hashtag.builder().hashtagId(1L).title("spring").build()
        ));
    given(tagRepository.saveAll(any()))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    List<Hashtag> result = tagService.ensureTags(names);

    // then
    assertThat(result)
        .hasSize(2)
        .extracting(Hashtag::getTitle)
        .containsExactlyInAnyOrder("spring", "hibernate");

    then(tagRepository).should().findAllByTitleIn(names);
    then(tagRepository).should().saveAll(any());
  }

  @Test
  void ensureTags_withDuplicates_filtersDuplicates() {
    // given
    List<String> namesWithDup = List.of("spring", "spring", "spring-boot");
    List<String> distinctNames = List.of("spring", "spring-boot");
    given(tagRepository.findAllByTitleIn(distinctNames))
        .willReturn(List.of(
            Hashtag.builder().hashtagId(1L).title("spring").build(),
            Hashtag.builder().hashtagId(2L).title("spring-boot").build()
        ));

    // when
    List<Hashtag> result = tagService.ensureTags(namesWithDup);

    // then
    assertThat(result)
        .hasSize(2)
        .extracting(Hashtag::getTitle)
        .containsExactlyInAnyOrder("spring", "spring-boot");

    then(tagRepository).should().findAllByTitleIn(distinctNames);
  }


  @Test
  void getTagTitlesByBookmarkId_returnsTagNames() {
    // given
    Long bookmarkId = 123L;
    List<String> expectedTags = List.of("spring", "java", "testing");
    given(bookmarkHashtagRepository.findTagNamesByBookmarkId(bookmarkId))
        .willReturn(expectedTags);

    // when
    List<String> actual = tagService.getTagTitlesByBookmarkId(bookmarkId);

    // then
    assertThat(actual).containsExactlyElementsOf(expectedTags);
  }

  @Test
  void getTagTitlesByBookmarkId_returnsEmptyListWhenNoTags() {
    // given
    Long bookmarkId = 456L;
    given(bookmarkHashtagRepository.findTagNamesByBookmarkId(bookmarkId))
        .willReturn(List.of());

    // when
    List<String> actual = tagService.getTagTitlesByBookmarkId(bookmarkId);

    // then
    assertThat(actual).isEmpty();
  }
}
