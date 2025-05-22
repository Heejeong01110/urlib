package com.heez.urlib.domain.tag.repository;

import com.heez.urlib.domain.tag.model.Hashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Hashtag, Long> {
  List<Hashtag> findAllByTitleIn(List<String> names);

}
