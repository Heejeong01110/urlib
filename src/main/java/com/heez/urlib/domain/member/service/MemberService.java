package com.heez.urlib.domain.member.service;

import com.heez.urlib.domain.auth.model.OAuth2UserInfo;
import com.heez.urlib.domain.member.controller.dto.MemberDetailResponse;
import com.heez.urlib.domain.member.model.Member;

public interface MemberService {

  Member findMemberOrCreate(OAuth2UserInfo userInfo);

  Member findById(Long memberId);

  MemberDetailResponse getProfile(Long memberId);
}
