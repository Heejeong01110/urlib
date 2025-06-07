package com.heez.urlib.domain.auth.security.resolver;

import com.heez.urlib.domain.auth.model.principal.UserPrincipal;
import com.heez.urlib.domain.auth.security.annotation.AuthUser;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    boolean hasAnnotation = parameter.hasParameterAnnotation(AuthUser.class);
    Class<?> paramType = parameter.getParameterType();

    return hasAnnotation &&
        (UserPrincipal.class.isAssignableFrom(paramType) ||
            Optional.class.isAssignableFrom(paramType));
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    AuthUser authUser = parameter.getParameterAnnotation(AuthUser.class);
    boolean required = authUser != null && authUser.required();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()
        || auth instanceof AnonymousAuthenticationToken) {
      if (required) {
        throw new AccessDeniedException("인증된 사용자만 접근할 수 있습니다.");
      }
      return wrap(parameter, Optional.empty());
    }

    Object principal = auth.getPrincipal();

    if (!(principal instanceof UserPrincipal userPrincipal)) {
      if (required) {
        throw new AccessDeniedException("UserPrincipal 타입의 인증 정보가 필요합니다.");
      }
      return wrap(parameter, Optional.empty());
    }

    return wrap(parameter, Optional.of(userPrincipal));
  }

  private Object wrap(MethodParameter parameter, Optional<UserPrincipal> optionalUser) {
    if (Optional.class.isAssignableFrom(parameter.getParameterType())) {
      return optionalUser;
    }
    return optionalUser.orElse(null);
  }
}
