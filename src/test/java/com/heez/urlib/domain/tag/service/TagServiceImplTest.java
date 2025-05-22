package com.heez.urlib.domain.tag.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.heez.urlib.domain.tag.model.Hashtag;
import com.heez.urlib.domain.tag.repository.TagRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

  @Mock
  private TagRepository tagRepository;

  @InjectMocks
  private TagServiceImpl tagService;

  @BeforeEach
  void setUp() {
  }


  @Test
  void ensureTags_existingNames_returnsExistingEntities() {
    // given
    List<String> names = List.of("spring", "java");
    given(tagRepository.findAllByNameIn(names))
        .willReturn(List.of(
            Hashtag.builder().hashtagId(1L).name("spring").build(),
            Hashtag.builder().hashtagId(2L).name("java").build()
        ));

    // when
    List<Hashtag> result = tagService.ensureTags(names);

    // then
    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(h -> "spring".equals(h.getName())));
    assertTrue(result.stream().anyMatch(h -> "java".equals(h.getName())));
    then(tagRepository).should().findAllByNameIn(names);
    then(tagRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  void ensureTags_newNames_createsAndReturnsAll() {
    // given
    List<String> names = List.of("spring", "hibernate");
    given(tagRepository.findAllByNameIn(names))
        .willReturn(List.of(
            Hashtag.builder().hashtagId(1L).name("spring").build()
        ));
    given(tagRepository.saveAll(any()))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    List<Hashtag> result = tagService.ensureTags(names);

    // then
    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(h -> "spring".equals(h.getName())));
    assertTrue(result.stream().anyMatch(h -> "hibernate".equals(h.getName())));
    then(tagRepository).should().findAllByNameIn(names);
    then(tagRepository).should().saveAll(any());
  }

  @Test
  void ensureTags_withDuplicates_filtersDuplicates() {
    // given
    List<String> namesWithDup = List.of("spring", "spring", "spring-boot");
    List<String> distinctNames = List.of("spring", "spring-boot");
    given(tagRepository.findAllByNameIn(distinctNames))
        .willReturn(List.of(
            Hashtag.builder().hashtagId(1L).name("spring").build(),
            Hashtag.builder().hashtagId(2L).name("spring-boot").build()
        ));

    // when
    List<Hashtag> result = tagService.ensureTags(namesWithDup);

    // then
    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(h -> "spring".equals(h.getName())));
    assertTrue(result.stream().anyMatch(h -> "spring-boot".equals(h.getName())));
    then(tagRepository).should().findAllByNameIn(distinctNames);
  }
}
