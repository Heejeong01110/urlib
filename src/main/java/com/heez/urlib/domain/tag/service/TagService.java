package com.heez.urlib.domain.tag.service;

import com.heez.urlib.domain.tag.model.Hashtag;
import java.util.List;

public interface TagService {
  List<Hashtag> ensureTags(List<String> tags);
}
