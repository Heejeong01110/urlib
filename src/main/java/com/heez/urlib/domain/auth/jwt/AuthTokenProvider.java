package com.heez.urlib.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

  private final Key KEY;

  private static final String AUTHORITIES_KEY = "role";

  public AuthTokenProvider(@Value("${security.jwt.secret-key}") String secretKey) {
    this.KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public String generateAccessToken(String name, String email, String role) {
    return generateToken(name, email, role, accessTokenExpiry);

  }

  public String createRefreshToken(String name, String email, String role) {
    return generateToken(name, email, role, refreshTokenExpiry);
  }

  //토큰 생성
  public String generateToken(String name, String email, String role, String expiry) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("name", name);
    claims.put("role", role);

    return Jwts
        .builder()
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(getExpiryDate(expiry))
        .signWith(KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  //토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parserBuilder()
          .setSigningKey(KEY)
          .build().parseClaimsJws(token);
      return claims.getBody().getExpiration().after(new Date(System.currentTimeMillis()));
    } catch (Exception e) {
      return false;
    }
  }

  //토큰에서 Authentication 객체를 가져오는 메서드
  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(KEY)
        .build().parseClaimsJws(token).getBody();

    List<SimpleGrantedAuthority> authorities = Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
        .map(SimpleGrantedAuthority::new)
        .toList();

    String email = (String) claims.get("email");

    return new UsernamePasswordAuthenticationToken(email, null, authorities);
  }

  //토큰 만료일자 설정
  public static Date getExpiryDate(String expiry) {
    return new Date(System.currentTimeMillis() + Long.parseLong(expiry));
  }

}
