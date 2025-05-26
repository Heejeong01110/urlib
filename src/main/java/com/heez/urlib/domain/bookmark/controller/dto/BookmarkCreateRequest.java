package com.heez.urlib.domain.bookmark.controller.dto;

import com.heez.urlib.domain.link.controller.dto.BaseLinkRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record BookmarkCreateRequest(

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    String title,

    @Size(max = 500, message = "설명은 최대 500자까지 입력 가능합니다.")
    String description,
    String imageUrl,
    Boolean visibleToOthers,
    List<String> tags,
    List<BaseLinkRequest> links
) {

}
