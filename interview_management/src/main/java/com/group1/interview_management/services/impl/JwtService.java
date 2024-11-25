package com.group1.interview_management.services.impl;

import org.springframework.stereotype.Service;

import com.group1.interview_management.common.EmailUtils;
import com.group1.interview_management.common.JwtName;
import com.group1.interview_management.dto.JwtTokenDTO;
import com.group1.interview_management.entities.User;
import com.group1.interview_management.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
     @Value("${jwt.secret-key}")
     private String jwtSecret;
     @Value("${jwt.expiration}")
     private Integer jwtExpiration;
     @Value("${jwt.refresh-token.expiration}")
     private Integer refreshExpiration;
     private final UserRepository userRepository;
     private final MessageSource messageSource;

     public String extractSubject(String token) {
          return extractClaim(token, Claims::getSubject);
     }

     public String extractUsername(String token) {
          return extractClaim(token, (claims) -> claims.get("username", String.class));
     }

     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
          final Claims claims = extractAllClaims(token);
          return claimsResolver.apply(claims);
     }

     private Claims extractAllClaims(String token) {
          return Jwts
                    .parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
     }

     private Key getSigningKey() {
          byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
          return Keys.hmacShaKeyFor(keyBytes);
     }

     public String generateRefreshToken(UserDetails user) {
          return Jwts
                    .builder()
                    .setSubject(user.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + (refreshExpiration)))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
     }

     public String generateAccessToken(
               Map<String, Object> extraClaims,
               UserDetails userDetails) {
          var authorities = userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .toList();
          Map<String, Object> claims = new HashMap<>();
          claims.put("email", userDetails.getUsername());
          claims.put("username", EmailUtils.extractMail(userDetails.getUsername()));
          if (extraClaims != null) {
               claims.putAll(extraClaims);
          }
          return Jwts
                    .builder()
                    .setClaims(claims)
                    .claim("authorities", authorities)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
     }

     public JwtTokenDTO createAuthToken(User user) {
          user.setRefreshToken(generateRefreshToken(user));
          userRepository.save(user);
          var jwtToken = generateAccessToken(null, user);
          return JwtTokenDTO
                    .builder()
                    .accessToken(jwtToken)
                    .refreshToken(user.getRefreshToken())
                    .build();
     }

     public JwtTokenDTO refreshToken(String refreshToken) throws IOException {
          var user = this.userRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> {
                         String errorMessage = messageSource.getMessage("jwttoken.notfound", null,
                                   Locale.getDefault());
                         return new ExpiredJwtException(null, null, errorMessage);
                    });
          if (isTokenValid(refreshToken, user)) {
               var jwtToken = generateAccessToken(null, user);
               return JwtTokenDTO
                         .builder()
                         .accessToken(jwtToken)
                         .refreshToken(user.getRefreshToken())
                         .build();
          } else {
               String errorMessage = messageSource.getMessage("jwttoken.invalid", null,
                         Locale.getDefault());
               throw new ExpiredJwtException(null, null, errorMessage);
          }

     }

     public void setTokenInsideCookie(HttpServletResponse servletResponse, JwtTokenDTO tokenDTO) {
          Cookie cookie = new Cookie(JwtName.ACCESS_TOKEN.getValue(), tokenDTO.getAccessToken());
          cookie.setHttpOnly(true);
          cookie.setSecure(true);
          cookie.setPath("/");
          cookie.setMaxAge(jwtExpiration);
          servletResponse.addCookie(cookie);

          Cookie refreshCookie = new Cookie(JwtName.REFRESH_TOKEN.getValue(), tokenDTO.getRefreshToken());
          refreshCookie.setHttpOnly(true);
          refreshCookie.setSecure(true);
          refreshCookie.setPath("/");
          refreshCookie.setMaxAge(refreshExpiration);
          servletResponse.addCookie(refreshCookie);
     }

     public boolean isTokenValid(String token, UserDetails userDetails) {
          final String username = extractSubject(token);
          return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
     }

     /**
      * Check if the token is expired
      * @param token
      * @return true if the token is expired, false otherwise
      */
     private boolean isTokenExpired(String token) {
          return extractExpiration(token).before(new Date());
     }

     private Date extractExpiration(String token) {
          return extractClaim(token, Claims::getExpiration);
     }
}
