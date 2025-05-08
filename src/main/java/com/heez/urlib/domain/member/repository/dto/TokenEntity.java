package com.heez.urlib.domain.member.repository.dto;

import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;

public record TokenEntity(
    Email email,
    Role role
) {

}
