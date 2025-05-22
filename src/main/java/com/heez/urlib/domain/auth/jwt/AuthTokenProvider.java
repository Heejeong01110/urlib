package com.heez.urlib.domain.auth.jwt;

import com.heez.urlib.domain.auth.exception.ExpiredRefreshTokenException;
import com.heez.urlib.domain.auth.exception.InvalidJwtFormatException;
import com.heez.urlib.domain.auth.exception.InvalidJwtSignatureException;
import com.heez.urlib.domain.auth.exception.InvalidRefreshTokenException;
import com.heez.urlib.domain.auth.exception.JwtTokenProcessingException;
import com.heez.urlib.domain.auth.exception.MissingJwtTokenException;
import com.heez.urlib.domain.auth.exception.TokenExpiredException;
import com.heez.urlib.domain.auth.exception.UnsupportedJwtTokenException;
import com.heez.urlib.domain.auth.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AuthTokenProvider {

  private final Key KEY;
  @Value("${spring.security.jwt.access-token-expiry}")
  private String accessTokenExpiry;
  @Value("${spring.security.jwt.refresh-token-expiry}")
  private String refreshTokenExpiry;

  public AuthTokenProvider(@Value("${spring.security.jwt.secret-key}") String secretKey) {
    this.KEY = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
  }

  //토큰 만료일자 설정
  public static Date getExpiryDate(String expiry) {
    long expiryInMs = TimeUnit.SECONDS.toMillis(Long.parseLong(expiry));
    return new Date(System.currentTimeMillis() + expiryInMs);
  }

  public String generateAccessToken(Long memberId, String email,
      List<SimpleGrantedAuthority> authorities) {
    List<String> roles = authorities.stream()
        .map(SimpleGrantedAuthority::getAuthority)
        .toList();

    Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));
    claims.put("email", email);
    claims.put("roles", roles);

    return Jwts
        .builder()
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(getExpiryDate(accessTokenExpiry))
        .signWith(KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(Long memberId) {
    return Jwts
        .builder()
        .setSubject(String.valueOf(memberId))
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(getExpiryDate(refreshTokenExpiry))
        .signWith(KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  //토큰 유효성 검증
  public void validateAccessToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(KEY)
          .setAllowedClockSkewSeconds(60)
          .build().parseClaimsJws(token);
    } catch (ExpiredJwtException e) {// — Access Token이 만료된 경우
      throw new TokenExpiredException();
    } catch (UnsupportedJwtException e) {// — 토큰 포맷은 올바르나, 현재 시스템에서 지원하지 않는 JWT인 경우
      throw new UnsupportedJwtTokenException();
    } catch (MalformedJwtException e) {// — JWT 구조 자체가 잘못되어 파싱이 불가능한 경우
      throw new InvalidJwtFormatException();
    } catch (SignatureException e) {// — 서명 검증에 실패한 경우 (토큰 위조 의심)
      throw new InvalidJwtSignatureException();
    } catch (IllegalArgumentException e) {// — 토큰이 `null`이거나, 빈 문자열 등 파싱 전에 잘못 넘어온 경우
      throw new MissingJwtTokenException();
    } catch (Exception e) {// — 그 외 JWT 관련 예외 혹은 기타 예외
      throw new JwtTokenProcessingException();
    }
  }

  public void validateRefreshToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(KEY)
          .setAllowedClockSkewSeconds(60)
          .build()
          .parseClaimsJws(token);
    } catch (ExpiredJwtException e) {
      throw new ExpiredRefreshTokenException();
    } catch (JwtException | IllegalArgumentException e) {
      throw new InvalidRefreshTokenException();
    }
  }

  //토큰에서 Authentication 객체를 가져오는 메서드
  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(KEY)
        .build().parseClaimsJws(token).getBody();

    String email = claims.getSubject();
    String nickname = claims.get("nickname", String.class);

    List<?> rawRoles = claims.get("roles", List.class);
    List<String> roles = rawRoles.stream()
        .map(Object::toString)
        .toList();

    List<SimpleGrantedAuthority> authorities = roles.stream()
        .map(SimpleGrantedAuthority::new)
        .toList();

    CustomUserDetails customUserDetails = new CustomUserDetails(email, nickname, authorities);

    return new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
  }

}
