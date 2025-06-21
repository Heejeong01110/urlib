package com.heez.urlib.domain.comment.repository;

import com.heez.urlib.domain.comment.model.Comment;
import com.heez.urlib.domain.comment.service.dto.CommentDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("""
          SELECT c
          FROM Comment c
          WHERE c.bookmark.bookmarkId = :bookmarkId
            AND c.parentComment IS NULL
          ORDER BY c.createdAt DESC
      """)
  Page<CommentDetailProjection> findRootCommentsByBookmarkId(
      @Param("bookmarkId") Long bookmarkId,
      Pageable pageable);

  @Query("""
          SELECT c
          FROM Comment c
          WHERE c.parentComment.commentId IN :parentCommentId
          ORDER BY c.createdAt ASC
      """)
  Page<CommentDetailProjection> findRepliesByCommentId(
      @Param("commentId") Long commentId,
      Pageable pageable);
}
