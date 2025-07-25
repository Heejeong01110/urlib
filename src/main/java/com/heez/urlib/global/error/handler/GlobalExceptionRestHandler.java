package com.heez.urlib.global.error.handler;

import static com.heez.urlib.global.error.handler.ErrorCode.ILLEGAL_ARGUMENT_ERROR;
import static com.heez.urlib.global.error.handler.ErrorCode.ILLEGAL_STATE_ERROR;
import static com.heez.urlib.global.error.handler.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.heez.urlib.global.error.handler.ErrorCode.INVALID_INPUT_VALUE_ERROR;
import static com.heez.urlib.global.error.handler.ErrorCode.INVALID_METHOD_ERROR;
import static com.heez.urlib.global.error.handler.ErrorCode.NOT_FOUND_ENTITY;
import static com.heez.urlib.global.error.handler.ErrorCode.REQUEST_BODY_MISSING_ERROR;
import static com.heez.urlib.global.error.handler.ErrorCode.REQUEST_PARAM_MISSING_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.heez.urlib.domain.auth.exception.DuplicateEmailByEmailTypeException;
import com.heez.urlib.domain.auth.exception.DuplicateEmailByKakaoTypeException;
import com.heez.urlib.domain.auth.exception.DuplicateNicknameException;
import com.heez.urlib.domain.auth.exception.ExpiredRefreshTokenException;
import com.heez.urlib.domain.auth.exception.InvalidJwtFormatException;
import com.heez.urlib.domain.auth.exception.InvalidJwtSignatureException;
import com.heez.urlib.domain.auth.exception.InvalidRefreshTokenException;
import com.heez.urlib.domain.auth.exception.JwtTokenProcessingException;
import com.heez.urlib.domain.auth.exception.MissingJwtTokenException;
import com.heez.urlib.domain.auth.exception.RefreshTokenUserNotFoundException;
import com.heez.urlib.domain.auth.exception.TokenExpiredException;
import com.heez.urlib.domain.auth.exception.TokenValidFailedException;
import com.heez.urlib.domain.auth.exception.UnsupportedJwtTokenException;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkException;
import com.heez.urlib.domain.bookmark.exception.AccessDeniedBookmarkModifyException;
import com.heez.urlib.domain.bookmark.exception.AlreadyLikedException;
import com.heez.urlib.domain.bookmark.exception.AlreadyUnlikedException;
import com.heez.urlib.domain.bookmark.exception.BookmarkNotFoundException;
import com.heez.urlib.domain.bookmark.exception.BookmarkShareNotFoundException;
import com.heez.urlib.domain.bookmark.exception.GptResponseParsingException;
import com.heez.urlib.domain.comment.exception.AccessDeniedCommentModifyException;
import com.heez.urlib.domain.comment.exception.CommentNotFoundException;
import com.heez.urlib.domain.link.exception.LinkNotFoundException;
import com.heez.urlib.domain.member.exception.AlreadyFollowingException;
import com.heez.urlib.domain.member.exception.MemberNotFoundException;
import com.heez.urlib.domain.member.exception.NotFollowingException;
import com.heez.urlib.domain.member.exception.SelfFollowException;
import com.heez.urlib.global.error.exception.AbstractGlobalException;
import com.heez.urlib.global.error.exception.AbstractJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class GlobalExceptionRestHandler {

  // [Exception] 객체 혹은 파라미터의 데이터 값이 유효하지 않은 경우
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.warn("Handle MethodArgumentNotValidException", e.getMessage());
    final ErrorResponse response = ErrorResponse.of(INVALID_METHOD_ERROR, e.getBindingResult());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // [Exception] 클라이언트에서 request의 '파라미터로' 데이터가 넘어오지 않았을 경우
  @ExceptionHandler(MissingServletRequestParameterException.class)
  protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderExceptionException(
      MissingServletRequestParameterException ex) {
    log.warn("Handle MissingServletRequestParameterException", ex);
    final ErrorResponse response = ErrorResponse.of(REQUEST_PARAM_MISSING_ERROR, ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  /*
    [Exception] enum type 일치하지 않아 binding 못할 경우
    주로 @RequestParam enum으로 binding 못했을 경우 발생
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    log.warn("Handle MethodArgumentTypeMismatchException", e);
    final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE_ERROR, e.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // [Exception] com.fasterxml.jackson.core 내에 Exception 발생하는 경우
  @ExceptionHandler(JsonProcessingException.class)
  protected ResponseEntity<ErrorResponse> handleJsonProcessingException(
      JsonProcessingException ex) {
    log.warn("handleJsonProcessingException", ex);
    final ErrorResponse response = ErrorResponse.of(REQUEST_BODY_MISSING_ERROR, ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // [Exception] @ModelAttribute 으로 binding error 발생할 경우
  @ExceptionHandler(BindException.class)
  protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
    log.warn("Handle BindException : ", e);
    final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE_ERROR,
        e.getBindingResult());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // [Exception] ContentType이 적절하지 않은 경우
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  protected ResponseEntity<ErrorResponse> handleHttpMediaTypeException(
      HttpMediaTypeNotSupportedException e) {
    log.warn("Handle HttpMediaTypeNotSupportedException : ", e);
    final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE_ERROR, e.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // [Exception] 자원이 존재하지 않는 경우
  @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
  protected ResponseEntity<ErrorResponse> handleNotFoundException(
      ChangeSetPersister.NotFoundException e) {
    log.warn("Handle NotFoundException : ", e);
    final ErrorResponse response = ErrorResponse.of(NOT_FOUND_ENTITY, e.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleAllException(IllegalArgumentException e) {
    log.error("Handle Exception :", e);
    final ErrorResponse response = ErrorResponse.of(ILLEGAL_ARGUMENT_ERROR, e.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleAllException(IllegalStateException e) {
    log.error("Handle Exception :", e);
    final ErrorResponse response = ErrorResponse.of(ILLEGAL_STATE_ERROR, e.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  // [Exception] 서버에 정의되지 않은 모든 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
    log.error("Handle Exception :", e);
    final ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR, e.getMessage());
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
  }

  @ExceptionHandler(SelfFollowException.class)
  protected ResponseEntity<ErrorResponse> handleSelfFollowException(AbstractGlobalException ex) {
    log.error("Handle {} : {}", ex.getClass().getSimpleName(), ex.getMessage());
    final ErrorResponse response = ErrorResponse.of(ex.getErrorCode(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler({
      RefreshTokenUserNotFoundException.class,
      ExpiredRefreshTokenException.class,
      InvalidRefreshTokenException.class,
      InvalidJwtFormatException.class,
      InvalidJwtSignatureException.class,
      JwtTokenProcessingException.class,
      MissingJwtTokenException.class,
      TokenExpiredException.class,
      TokenValidFailedException.class,
      UnsupportedJwtTokenException.class
  })
  protected ResponseEntity<ErrorResponse> handleUnauthorizedExceptions(AbstractJwtException ex) {
    log.error("Handle {} : {}", ex.getClass().getSimpleName(), ex.getMessage());
    final ErrorResponse response = ErrorResponse.of(ex.getErrorCode(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @ExceptionHandler({
      AccessDeniedBookmarkException.class,
      AccessDeniedCommentModifyException.class,
      AccessDeniedBookmarkModifyException.class
  })
  protected ResponseEntity<ErrorResponse> handleForbiddenException(AbstractGlobalException ex) {
    log.error("Handle {} : {}", ex.getClass().getSimpleName(), ex.getMessage());
    final ErrorResponse response = ErrorResponse.of(ex.getErrorCode(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  @ExceptionHandler({
      BookmarkNotFoundException.class,
      LinkNotFoundException.class,
      BookmarkShareNotFoundException.class,
      CommentNotFoundException.class,
      MemberNotFoundException.class
  })
  protected ResponseEntity<ErrorResponse> handleNotFoundException(AbstractGlobalException ex) {
    log.error("Handle {} : {}", ex.getClass().getSimpleName(), ex.getMessage());
    final ErrorResponse response = ErrorResponse.of(ex.getErrorCode(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler({
      GptResponseParsingException.class,
      DuplicateEmailByEmailTypeException.class,
      DuplicateEmailByKakaoTypeException.class,
      DuplicateNicknameException.class,
      AlreadyLikedException.class,
      AlreadyUnlikedException.class,
      AlreadyFollowingException.class,
      NotFollowingException.class
  })
  protected ResponseEntity<ErrorResponse> handleConflictException(AbstractGlobalException ex) {
    log.error("Handle {} : {}", ex.getClass().getSimpleName(), ex.getMessage());
    final ErrorResponse response = ErrorResponse.of(ex.getErrorCode(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

}
