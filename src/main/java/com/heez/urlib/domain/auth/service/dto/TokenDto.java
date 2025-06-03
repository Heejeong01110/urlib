package com.heez.urlib.domain.auth.service.dto;

import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;

public record TokenDto(
    Email email,
    Role role
) {

}
