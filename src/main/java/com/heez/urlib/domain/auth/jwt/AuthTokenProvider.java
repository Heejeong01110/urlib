package com.heez.urlib.domain.auth.jwt;

import com.heez.urlib.domain.auth.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenProvider {

  private static final String AUTHORITIES_KEY = "role";
  private final Key KEY;
  @Value("${spring.security.jwt.access-token-expiry}")
  private String accessTokenExpiry;
  @Value("${spring.security.jwt.refresh-token-expiry}")
  private String refreshTokenExpiry;

  public AuthTokenProvider(@Value("${spring.security.jwt.secret-key}") String secretKey) {
    this.KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  //토큰 만료일자 설정
  public static Date getExpiryDate(String expiry) {
    return new Date(System.currentTimeMillis() + Long.parseLong(expiry));
  }

  public String generateAccessToken(String nickname, String email, List<SimpleGrantedAuthority> role) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("name", nickname);
    claims.put("roles", role);

    return Jwts
        .builder()
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(getExpiryDate(accessTokenExpiry))
        .signWith(KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(String email) {
    return Jwts
        .builder()
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(getExpiryDate(refreshTokenExpiry))
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
    } catch (Exception e) { //예외처리
      return false;
    }
  }

  //토큰에서 Authentication 객체를 가져오는 메서드
  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(KEY)
        .build().parseClaimsJws(token).getBody();

    String email = claims.getSubject();
    String nickname = claims.get("nickname", String.class);
    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority(claims.get("role", String.class)));
    CustomUserDetails customUserDetails = new CustomUserDetails(email, nickname, authorities);

    return new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
  }

}
