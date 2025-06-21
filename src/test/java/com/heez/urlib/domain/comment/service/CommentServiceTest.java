package com.heez.urlib.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
import com.heez.urlib.domain.bookmark.exception.BookmarkNotFoundException;
import com.heez.urlib.domain.bookmark.model.Bookmark;
import com.heez.urlib.domain.bookmark.service.BookmarkPermissionService;
import com.heez.urlib.domain.bookmark.service.BookmarkService;
import com.heez.urlib.domain.comment.controller.dto.CommentCreateRequest;
import com.heez.urlib.domain.comment.controller.dto.CommentDetailResponse;
import com.heez.urlib.domain.comment.controller.dto.CommentUpdateRequest;
import com.heez.urlib.domain.comment.exception.AccessDeniedCommentModifyException;
import com.heez.urlib.domain.comment.exception.CommentNotFoundException;
import com.heez.urlib.domain.comment.model.Comment;
import com.heez.urlib.domain.comment.repository.CommentRepository;
import com.heez.urlib.domain.comment.service.dto.CommentDetailProjection;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.model.Member;
import com.heez.urlib.domain.member.service.MemberService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private BookmarkPermissionService bookmarkPermissionService;

  @Mock
  private BookmarkService bookmarkService;

  @InjectMocks
  private CommentService commentService;

  @Test
  void getParentComments_validRequest_returnsComments() {
    // given
    Long memberId = 42L;
    Long bookmarkId = 100L;
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);

    Bookmark bookmark = Bookmark.builder().member(member).build();

    CommentDetailProjection.MemberInfo memberInfo = mock(CommentDetailProjection.MemberInfo.class);
    when(memberInfo.getId()).thenReturn(10L);
    when(memberInfo.getImageUrl()).thenReturn("http://example.com/image.png");
    CommentDetailProjection projection1 = mock(CommentDetailProjection.class);
    when(projection1.getCommentId()).thenReturn(10L);
    when(projection1.getMember()).thenReturn(memberInfo);
    when(projection1.getContent()).thenReturn("Sample Description");

    CommentDetailProjection.MemberInfo memberInfo2 = mock(CommentDetailProjection.MemberInfo.class);
    when(memberInfo2.getId()).thenReturn(11L);
    when(memberInfo2.getImageUrl()).thenReturn("http://example.com/image.png");
    CommentDetailProjection projection2 = mock(CommentDetailProjection.class);
    when(projection2.getCommentId()).thenReturn(11L);
    when(projection2.getMember()).thenReturn(memberInfo2);
    when(projection2.getContent()).thenReturn("Sample Description2");

    Page<CommentDetailProjection> commentPage = new PageImpl<>(List.of(projection1, projection2),
        pageable, 2);

    given(bookmarkService.findByBookmarkId(bookmarkId)).willReturn(bookmark);
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(commentRepository.findRootCommentsByBookmarkId(bookmarkId, pageable))
        .willReturn(commentPage);

    // when
    Page<CommentDetailResponse> result = commentService.getParentComments(
        Optional.of(memberId), bookmarkId, pageable);

    // then
    then(bookmarkService).should().findByBookmarkId(bookmarkId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
    then(commentRepository).should().findRootCommentsByBookmarkId(bookmarkId, pageable);

    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).id()).isEqualTo(10L);
    assertThat(result.getContent().get(0).content()).isEqualTo("Sample Description");
    assertThat(result.getContent().get(1).id()).isEqualTo(11L);
    assertThat(result.getContent().get(1).content()).isEqualTo("Sample Description2");
  }

  @Test
  void getParentComments_bookmarkNotFound_throwsException() {
    // given
    Long memberId = 42L;
    Long bookmarkId = 999L;
    Pageable pageable = PageRequest.of(0, 10);

    given(bookmarkService.findByBookmarkId(bookmarkId))
        .willThrow(new BookmarkNotFoundException());

    // when & then
    assertThatThrownBy(() ->
        commentService.getParentComments(Optional.of(memberId), bookmarkId, pageable))
        .isInstanceOf(BookmarkNotFoundException.class);

    then(bookmarkService).should().findByBookmarkId(bookmarkId);
    then(bookmarkPermissionService).shouldHaveNoInteractions();
    then(commentRepository).shouldHaveNoInteractions();
  }

  @Test
  void getParentComments_accessDenied_throwsException() {
    // given
    Long memberId = 42L;
    Long bookmarkId = 100L;
    Pageable pageable = PageRequest.of(0, 10);

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 10L);

    Bookmark bookmark = Bookmark.builder().member(owner).visibleToOthers(false).build();

    given(bookmarkService.findByBookmarkId(bookmarkId)).willReturn(bookmark);
    willThrow(new AccessDeniedBookmarkException())
        .given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));

    // when & then
    assertThatThrownBy(() ->
        commentService.getParentComments(Optional.of(memberId), bookmarkId, pageable))
        .isInstanceOf(AccessDeniedBookmarkException.class);

    then(bookmarkService).should().findByBookmarkId(bookmarkId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
    then(commentRepository).shouldHaveNoInteractions();
  }

  @Test
  void getChildrenComments_validRequest_returnsReplies() {
    // given
    Long memberId = 42L;
    Long parentCommentId = 10L;
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    // 북마크 + 부모 댓글 구성
    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);

    Bookmark bookmark = Bookmark.builder().member(member).build();

    Comment parentComment = Comment.builder()
        .bookmark(bookmark)
        .content("부모 댓글입니다.")
        .member(member)
        .build();
    ReflectionTestUtils.setField(parentComment, "commentId", parentCommentId);

    // 대댓글 프로젝션들
    CommentDetailProjection.MemberInfo memberInfo1 = mock(CommentDetailProjection.MemberInfo.class);
    when(memberInfo1.getId()).thenReturn(100L);
    when(memberInfo1.getImageUrl()).thenReturn("https://example.com/1.png");

    CommentDetailProjection reply1 = mock(CommentDetailProjection.class);
    when(reply1.getCommentId()).thenReturn(101L);
    when(reply1.getContent()).thenReturn("대댓글1");
    when(reply1.getMember()).thenReturn(memberInfo1);

    CommentDetailProjection.MemberInfo memberInfo2 = mock(CommentDetailProjection.MemberInfo.class);
    when(memberInfo2.getId()).thenReturn(200L);
    when(memberInfo2.getImageUrl()).thenReturn("https://example.com/2.png");

    CommentDetailProjection reply2 = mock(CommentDetailProjection.class);
    when(reply2.getCommentId()).thenReturn(102L);
    when(reply2.getContent()).thenReturn("대댓글2");
    when(reply2.getMember()).thenReturn(memberInfo2);

    Page<CommentDetailProjection> replyPage = new PageImpl<>(List.of(reply1, reply2), pageable, 2);

    // mocking
    given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(commentRepository.findRepliesByCommentId(parentCommentId, pageable)).willReturn(
        replyPage);

    // when
    Page<CommentDetailResponse> result = commentService.getChildrenComments(
        Optional.of(memberId), parentCommentId, pageable);

    // then
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
    then(commentRepository).should().findRepliesByCommentId(parentCommentId, pageable);

    List<CommentDetailResponse> content = result.getContent();
    assertThat(content.size()).isEqualTo(2);
    assertThat(content.get(0).id()).isEqualTo(101L);
    assertThat(content.get(0).content()).isEqualTo("대댓글1");
    assertThat(content.get(0).memberInfo().memberId()).isEqualTo(100L);
    assertThat(content.get(0).memberInfo().memberImageUrl()).isEqualTo("https://example.com/1.png");

    assertThat(content.get(1).id()).isEqualTo(102L);
    assertThat(content.get(1).content()).isEqualTo("대댓글2");
    assertThat(content.get(1).memberInfo().memberId()).isEqualTo(200L);
    assertThat(content.get(1).memberInfo().memberImageUrl()).isEqualTo("https://example.com/2.png");
  }

  @Test
  void getChildrenComments_commentNotFound_throwsException() {
    // given
    Long memberId = 42L;
    Long commentId = 999L;
    Pageable pageable = PageRequest.of(0, 10);

    // when & then
    assertThatThrownBy(() ->
        commentService.getChildrenComments(Optional.of(memberId), commentId, pageable)
    ).isInstanceOf(CommentNotFoundException.class);

  }

  @Test
  void getChildrenComments_accessDenied_throwsException() {
    // given
    Long memberId = 42L;
    Long commentId = 100L;
    Pageable pageable = PageRequest.of(0, 10);

    Member owner = Member.builder().build();
    ReflectionTestUtils.setField(owner, "memberId", 1L);

    Bookmark bookmark = Bookmark.builder().member(owner).visibleToOthers(false).build();

    Comment parentComment = Comment.builder()
        .bookmark(bookmark)
        .content("숨겨진 북마크 댓글")
        .member(owner)
        .build();
    ReflectionTestUtils.setField(parentComment, "commentId", commentId);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(parentComment));
    willThrow(new AccessDeniedBookmarkException())
        .given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));

    // when & then
    assertThatThrownBy(() ->
        commentService.getChildrenComments(Optional.of(memberId), commentId, pageable)
    ).isInstanceOf(AccessDeniedBookmarkException.class);

    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
  }

  @Test
  void createComment_validRequest_savesCommentAndReturnsResponse() {
    // given
    Long bookmarkId = 1L;
    Long memberId = 10L;
    String content = "댓글 내용";

    CommentCreateRequest request = CommentCreateRequest.builder()
        .content(content)
        .parentCommentId(null)
        .build();

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);
    ReflectionTestUtils.setField(member, "imageUrl", "http://example.com/profile.png");

    Bookmark bookmark = Bookmark.builder().member(member).build();

    Comment savedComment = Comment.builder()
        .content(content)
        .bookmark(bookmark)
        .member(member)
        .build();
    ReflectionTestUtils.setField(savedComment, "commentId", 123L);

    given(bookmarkService.findByBookmarkId(bookmarkId)).willReturn(bookmark);
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(memberService.findById(memberId)).willReturn(member);
    given(commentRepository.save(any())).willReturn(savedComment);

    // when
    CommentDetailResponse result = commentService.createComment(bookmarkId, memberId, request);

    // then
    then(bookmarkService).should().findByBookmarkId(bookmarkId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
    then(memberService).should().findById(memberId);

    assertThat(result.id()).isEqualTo(123L);
    assertThat(result.content()).isEqualTo(content);
    assertThat(result.memberInfo().memberId()).isEqualTo(memberId);
    assertThat(result.memberInfo().memberImageUrl()).isEqualTo("http://example.com/profile.png");
  }

  @Test
  void createComment_bookmarkNotFound_throwsException() {
    // given
    Long bookmarkId = 999L;
    Long memberId = 10L;
    CommentCreateRequest request = CommentCreateRequest.builder()
        .content("댓글")
        .parentCommentId(null)
        .build();

    given(bookmarkService.findByBookmarkId(bookmarkId))
        .willThrow(new BookmarkNotFoundException());

    // when & then
    assertThatThrownBy(() ->
        commentService.createComment(bookmarkId, memberId, request)
    ).isInstanceOf(BookmarkNotFoundException.class);

    then(bookmarkService).should().findByBookmarkId(bookmarkId);
    then(bookmarkPermissionService).shouldHaveNoInteractions();
    then(memberService).shouldHaveNoInteractions();
  }

  @Test
  void createComment_accessDenied_throwsException() {
    // given
    Long bookmarkId = 1L;
    Long memberId = 10L;
    CommentCreateRequest request = CommentCreateRequest.builder()
        .content("댓글")
        .parentCommentId(null)
        .build();

    Bookmark bookmark = Bookmark.builder().build();

    given(bookmarkService.findByBookmarkId(bookmarkId)).willReturn(bookmark);
    willThrow(new AccessDeniedBookmarkException())
        .given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));

    // when & then
    assertThatThrownBy(() ->
        commentService.createComment(bookmarkId, memberId, request)
    ).isInstanceOf(AccessDeniedBookmarkException.class);

    then(bookmarkService).should().findByBookmarkId(bookmarkId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
    then(memberService).shouldHaveNoInteractions();
  }

  @Test
  void createComment_memberNotFound_throwsException() {
    // given
    Long bookmarkId = 1L;
    Long memberId = 999L;
    CommentCreateRequest request = CommentCreateRequest.builder()
        .content("댓글")
        .parentCommentId(null)
        .build();

    Bookmark bookmark = Bookmark.builder().build();

    given(bookmarkService.findByBookmarkId(bookmarkId)).willReturn(bookmark);
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(memberService.findById(memberId)).willThrow(new MemberNotFoundException());

    // when & then
    assertThatThrownBy(() ->
        commentService.createComment(bookmarkId, memberId, request)
    ).isInstanceOf(MemberNotFoundException.class);

    then(bookmarkService).should().findByBookmarkId(bookmarkId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
    then(memberService).should().findById(memberId);
  }

  @Test
  void createReplyComment_validRequest_savesReplyAndReturnsResponse() {
    // given
    Long commentId = 100L;
    Long memberId = 10L;
    String content = "대댓글 내용";

    CommentCreateRequest request = CommentCreateRequest.builder()
        .content(content)
        .parentCommentId(commentId)
        .build();

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);
    ReflectionTestUtils.setField(member, "imageUrl", "http://example.com/profile.png");

    Bookmark bookmark = Bookmark.builder().member(member).build();

    Comment parentComment = Comment.builder()
        .content("부모 댓글")
        .bookmark(bookmark)
        .member(member)
        .build();
    ReflectionTestUtils.setField(parentComment, "commentId", commentId);

    Comment replyComment = Comment.builder()
        .content(content)
        .bookmark(bookmark)
        .member(member)
        .parentComment(parentComment)
        .build();
    ReflectionTestUtils.setField(replyComment, "commentId", 123L);

    given(memberService.findById(memberId)).willReturn(member);
    given(commentRepository.findById(commentId)).willReturn(Optional.of(parentComment));
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));
    given(commentRepository.save(any())).willReturn(replyComment);

    // when
    CommentDetailResponse result = commentService.createReplyComment(commentId, memberId, request);

    // then
    then(memberService).should().findById(memberId);
    then(commentRepository).should().findById(commentId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));

    assertThat(result.id()).isEqualTo(123L);
    assertThat(result.content()).isEqualTo(content);
    assertThat(result.memberInfo().memberId()).isEqualTo(memberId);
    assertThat(result.memberInfo().memberImageUrl()).isEqualTo("http://example.com/profile.png");
  }

  @Test
  void createReplyComment_commentNotFound_throwsException() {
    // given
    Long commentId = 999L;
    Long memberId = 10L;
    CommentCreateRequest request = CommentCreateRequest.builder()
        .content("대댓글")
        .parentCommentId(commentId)
        .build();

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);

    given(memberService.findById(memberId)).willReturn(member);
    given(commentRepository.findById(commentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        commentService.createReplyComment(commentId, memberId, request)
    ).isInstanceOf(CommentNotFoundException.class);

    then(memberService).should().findById(memberId);
    then(commentRepository).should().findById(commentId);
    then(bookmarkPermissionService).shouldHaveNoInteractions();
  }

  @Test
  void createReplyComment_accessDenied_throwsException() {
    // given
    Long commentId = 100L;
    Long memberId = 10L;
    CommentCreateRequest request = CommentCreateRequest.builder()
        .content("대댓글")
        .parentCommentId(commentId)
        .build();

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);

    Bookmark bookmark = Bookmark.builder().build();
    Comment parentComment = Comment.builder()
        .bookmark(bookmark)
        .member(member)
        .build();
    ReflectionTestUtils.setField(parentComment, "commentId", commentId);

    given(memberService.findById(memberId)).willReturn(member);
    given(commentRepository.findById(commentId)).willReturn(Optional.of(parentComment));
    willThrow(new AccessDeniedBookmarkException())
        .given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));

    // when & then
    assertThatThrownBy(() ->
        commentService.createReplyComment(commentId, memberId, request)
    ).isInstanceOf(AccessDeniedBookmarkException.class);

    then(memberService).should().findById(memberId);
    then(commentRepository).should().findById(commentId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
  }

  @Test
  void createReplyComment_replyToReply_throwsException() {
    // given
    Long commentId = 100L;
    Long memberId = 10L;
    CommentCreateRequest request = CommentCreateRequest.builder()
        .content("잘못된 대댓글")
        .parentCommentId(commentId)
        .build();

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);

    Bookmark bookmark = Bookmark.builder().member(member).build();

    Comment grandParent = Comment.builder().build();
    Comment replyToReply = Comment.builder()
        .bookmark(bookmark)
        .member(member)
        .parentComment(grandParent) // ❗ 대댓글의 대댓글임
        .build();
    ReflectionTestUtils.setField(replyToReply, "commentId", commentId);

    given(memberService.findById(memberId)).willReturn(member);
    given(commentRepository.findById(commentId)).willReturn(Optional.of(replyToReply));
    willDoNothing().given(bookmarkPermissionService).isVisible(bookmark, Optional.of(memberId));

    // when & then
    assertThatThrownBy(() ->
        commentService.createReplyComment(commentId, memberId, request)
    ).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("대댓글의 대댓글은 허용되지 않습니다.");

    then(memberService).should().findById(memberId);
    then(commentRepository).should().findById(commentId);
    then(bookmarkPermissionService).should().isVisible(bookmark, Optional.of(memberId));
  }

  @Test
  void updateComment_validRequest_updatesAndReturnsResponse() {
    // given
    Long memberId = 10L;
    Long commentId = 123L;
    String newContent = "수정된 댓글 내용";

    CommentUpdateRequest request = CommentUpdateRequest.builder()
        .content(newContent)
        .build();

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);
    ReflectionTestUtils.setField(member, "imageUrl", "http://example.com/me.png");

    Comment comment = Comment.builder()
        .content("기존 댓글")
        .member(member)
        .build();
    ReflectionTestUtils.setField(comment, "commentId", commentId);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(memberService.findById(memberId)).willReturn(member);

    // when
    CommentDetailResponse result = commentService.updateComment(memberId, commentId, request);

    // then
    then(commentRepository).should().findById(commentId);
    then(memberService).should().findById(memberId);

    assertThat(result.id()).isEqualTo(commentId);
    assertThat(result.content()).isEqualTo(newContent);
    assertThat(result.memberInfo().memberId()).isEqualTo(memberId);
    assertThat(result.memberInfo().memberImageUrl()).isEqualTo("http://example.com/me.png");
  }

  @Test
  void updateComment_commentNotFound_throwsException() {
    // given
    Long memberId = 10L;
    Long commentId = 999L;

    CommentUpdateRequest request = CommentUpdateRequest.builder()
        .content("수정하려는 내용")
        .build();

    given(commentRepository.findById(commentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        commentService.updateComment(memberId, commentId, request)
    ).isInstanceOf(CommentNotFoundException.class);

    then(commentRepository).should().findById(commentId);
    then(memberService).shouldHaveNoInteractions();
  }

  @Test
  void updateComment_accessDenied_throwsException() {
    // given
    Long memberId = 10L;
    Long commentId = 100L;

    CommentUpdateRequest request = CommentUpdateRequest.builder()
        .content("누군가의 댓글을 수정 시도")
        .build();

    Member otherMember = Member.builder().build();
    ReflectionTestUtils.setField(otherMember, "memberId", 999L); // 다른 사용자

    Comment comment = Comment.builder()
        .content("남의 댓글")
        .member(otherMember)
        .build();
    ReflectionTestUtils.setField(comment, "commentId", commentId);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

    // when & then
    assertThatThrownBy(() ->
        commentService.updateComment(memberId, commentId, request)
    ).isInstanceOf(AccessDeniedCommentModifyException.class);

    then(commentRepository).should().findById(commentId);
    then(memberService).shouldHaveNoInteractions();
  }

  @Test
  void deleteComment_validRequest_deletesComment() {
    // given
    Long memberId = 10L;
    Long commentId = 123L;

    Member member = Member.builder().build();
    ReflectionTestUtils.setField(member, "memberId", memberId);

    Comment comment = Comment.builder()
        .content("삭제할 댓글")
        .member(member)
        .build();
    ReflectionTestUtils.setField(comment, "commentId", commentId);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

    // when
    commentService.deleteComment(memberId, commentId);

    // then
    then(commentRepository).should().findById(commentId);
    then(commentRepository).should().delete(comment);
  }

  @Test
  void deleteComment_commentNotFound_throwsException() {
    // given
    Long memberId = 10L;
    Long commentId = 999L;

    given(commentRepository.findById(commentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        commentService.deleteComment(memberId, commentId)
    ).isInstanceOf(CommentNotFoundException.class);

    then(commentRepository).should().findById(commentId);
    then(commentRepository).should(never()).delete(any());
  }

  @Test
  void deleteComment_accessDenied_throwsException() {
    // given
    Long memberId = 10L;
    Long commentId = 100L;

    Member otherMember = Member.builder().build();
    ReflectionTestUtils.setField(otherMember, "memberId", 999L); // 다른 사용자

    Comment comment = Comment.builder()
        .content("남의 댓글")
        .member(otherMember)
        .build();
    ReflectionTestUtils.setField(comment, "commentId", commentId);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

    // when & then
    assertThatThrownBy(() ->
        commentService.deleteComment(memberId, commentId)
    ).isInstanceOf(AccessDeniedCommentModifyException.class);

    then(commentRepository).should().findById(commentId);
    then(commentRepository).should(never()).delete(any());
  }

}
