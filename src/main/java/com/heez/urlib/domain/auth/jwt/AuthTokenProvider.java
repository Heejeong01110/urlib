package com.heez.urlib.domain.auth.jwt;

import com.heez.urlib.domain.auth.model.CustomUserDetails;
import com.heez.urlib.global.exception.ErrorMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component
public class AuthTokenProvider {

  @Value("${security.jwt.access-token-expiry}")
  private String accessTokenExpiry;

  @Value("${security.jwt.refresh-token-expiry}")
  private String refreshTokenExpiry;

  private final Key key;

  private static final String AUTHORITIES_KEY = "role";

  public AuthTokenProvider(@Value("${security.jwt.secret-key}") String secretKey) {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public String createAccessToken(Long memberId) {
    return createToken(memberId, accessTokenExpiry);

  }

  public String createRefreshToken(Long memberId) {
    return createToken(memberId, refreshTokenExpiry);
  }

  public String createToken(Long memberId,String expiry) {
    return Jwts
        .builder()
        .claim("memberId", memberId)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(getExpiryDate(expiry))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  //토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build().parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      throw new IllegalArgumentException(ErrorMessage.UNAUTHORIZED_INVALID_TOKEN.getMessage()); //수정 필요
    }
  }

  //토큰에서 Authentication 객체를 가져오는 메서드
  public Authentication getAuthentication(String token) {

    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build().parseClaimsJws(token).getBody();

    List<SimpleGrantedAuthority> authorities = Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
        .map(SimpleGrantedAuthority::new)
        .toList();

    Long memberId = (Long) claims.get("memberId");

    CustomUserDetails customUserDetails = CustomUserDetails.builder()
        .memberId(memberId)
        .authorities(authorities)
        .build();

    return new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
  }

  //토큰 만료일자 설정
  public static Date getExpiryDate(String expiry) {
    return new Date(System.currentTimeMillis() + Long.parseLong(expiry));
  }

}
