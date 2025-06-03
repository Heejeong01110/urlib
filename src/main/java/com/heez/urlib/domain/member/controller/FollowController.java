package com.heez.urlib.domain.member.controller;

import com.heez.urlib.domain.auth.model.UserPrincipal;
import com.heez.urlib.domain.member.controller.dto.FollowStatusResponse;
import com.heez.urlib.domain.member.controller.dto.MemberSummaryResponse;
import com.heez.urlib.domain.member.model.AuthUser;
import com.heez.urlib.domain.member.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FollowController {

  private final FollowService followService;

  @GetMapping("/{memberId}/follow")
  public ResponseEntity<FollowStatusResponse> getFollowStatus(
      @AuthUser UserPrincipal userPrincipal,
      @PathVariable Long memberId
  ) {
    return ResponseEntity.ok(followService.getFollowStatus(memberId, userPrincipal.getMemberId()));
  }

  @PostMapping("/{memberId}/follow")
  public ResponseEntity<Void> followOther(
      @AuthUser UserPrincipal userPrincipal,
      @PathVariable Long memberId
  ) {
    followService.follow(memberId, userPrincipal.getMemberId());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{memberId}/unfollow")
  public ResponseEntity<Void> unfollowOther(
      @AuthUser UserPrincipal userPrincipal,
      @PathVariable Long memberId
  ) {
    followService.unfollow(memberId, userPrincipal.getMemberId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{memberId}/following")
  public ResponseEntity<Page<MemberSummaryResponse>> getFollowingList(
      @PathVariable Long memberId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowingList(memberId, pageable));
  }


  @GetMapping("/{memberId}/follower")
  public ResponseEntity<Page<MemberSummaryResponse>> getFollowerList(
      @PathVariable Long memberId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowerList(memberId, pageable));
  }

  @GetMapping("/me/following")
  public ResponseEntity<Page<MemberSummaryResponse>> getMyFollowingList(
      @AuthUser UserPrincipal userPrincipal,
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowingList(userPrincipal.getMemberId(), pageable));
  }

  @GetMapping("/me/follower")
  public ResponseEntity<Page<MemberSummaryResponse>> getMyFollowerList(
      @AuthUser UserPrincipal userPrincipal,
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowerList(userPrincipal.getMemberId(), pageable));
  }

}
