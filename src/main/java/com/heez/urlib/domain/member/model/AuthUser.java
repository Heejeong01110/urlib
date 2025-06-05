package com.heez.urlib.domain.member.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 컨트롤러 메서드 파라미터에 인증된 사용자 정보를 주입합니다.
 required = true 인 경우, UserPrincipal 리턴. 인증되지 않은 요청은 AccessDeniedException이 발생합니다.
 required = false 인 경우, Optional<UserPrincipal> 리턴.
 ***/
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthUser {

  boolean required() default false;
}

