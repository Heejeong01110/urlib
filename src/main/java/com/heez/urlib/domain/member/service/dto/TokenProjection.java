package com.heez.urlib.domain.member.service.dto;

import com.heez.urlib.domain.member.model.Role;
import com.heez.urlib.domain.member.model.vo.Email;

public interface TokenProjection {

  Email getEmail();

  Role getRole();

}
