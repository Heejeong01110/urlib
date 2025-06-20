package com.heez.urlib.domain.member.controller;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import com.heez.urlib.domain.member.controller.dto.MemberDetailResponse;
import com.heez.urlib.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/me")
  public ResponseEntity<MemberDetailResponse> getMyProfile(
      @AuthUser(required = true) UserPrincipal userPrincipal
  ) {
    return ResponseEntity.ok(
        memberService.getProfile(userPrincipal.getMemberId()));
  }


  @GetMapping("/{memberId}")
  public ResponseEntity<MemberDetailResponse> getProfile(
      @PathVariable("memberId") Long memberId
  ) {
    return ResponseEntity.ok(
        memberService.getProfile(memberId));
  }
}
