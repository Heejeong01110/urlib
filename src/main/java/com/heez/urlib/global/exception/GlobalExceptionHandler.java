package com.heez.urlib.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<String> handleNotFound(NotFoundException exception) {
    log.error(exception.getMessage());
    return ResponseEntity.badRequest().body(exception.getMessage());
  }

}
