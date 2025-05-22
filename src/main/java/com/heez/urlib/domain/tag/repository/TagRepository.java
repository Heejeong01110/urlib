package com.heez.urlib.domain.tag.repository;

import com.heez.urlib.domain.tag.model.Hashtag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Hashtag, Long> {

  Optional<Hashtag> findHashtagByName(String name);
  List<Hashtag> findAllByNameIn(List<String> names);
}
