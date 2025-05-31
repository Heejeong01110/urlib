package com.heez.urlib.domain.member.controller;

import com.heez.urlib.domain.auth.model.CustomOAuth2User;
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
      @AuthUser CustomOAuth2User oauth2User,
      @PathVariable Long memberId
  ) {
    return ResponseEntity.ok(followService.getFollowStatus(memberId, oauth2User.getMemberId()));
  }

  @PostMapping("/{memberId}/follow")
  public ResponseEntity<Void> followOther(
      @AuthUser CustomOAuth2User oauth2User,
      @PathVariable Long memberId
  ) {
    followService.follow(memberId, oauth2User.getMemberId());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{memberId}/follow")
  public ResponseEntity<Void> unfollowOther(
      @AuthUser CustomOAuth2User oauth2User,
      @PathVariable Long memberId
  ) {
    followService.unfollow(memberId, oauth2User.getMemberId());
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
      @AuthUser CustomOAuth2User oauth2User,
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowingList(oauth2User.getMemberId(), pageable));
  }

  @GetMapping("/me/follower")
  public ResponseEntity<Page<MemberSummaryResponse>> getMyFollowerList(
      @AuthUser CustomOAuth2User oauth2User,
      Pageable pageable
  ) {
    return ResponseEntity.ok(followService.getFollowerList(oauth2User.getMemberId(), pageable));
  }

}
