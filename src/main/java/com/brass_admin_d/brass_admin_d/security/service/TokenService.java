package com.brass_admin_d.brass_admin_d.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TokenService {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final Duration accessTokenValidityTime;
    private final Duration refreshTokenValidityTime;


    public TokenService(
            @Value("${jwt.access.secret}") String accessKey,
            @Value("${jwt.refresh.secret}") String refreshKey,
            @Value("${jwt.access-token.lifetime}") Duration accessKeyLifeTime,
            @Value("${jwt.refresh-token.lifetime}") Duration refreshKeyLifeTime
    ) {
        this.accessKey = getSignInKey(accessKey);
        this.refreshKey = getSignInKey(refreshKey);
        this.accessTokenValidityTime = accessKeyLifeTime;
        this.refreshTokenValidityTime = refreshKeyLifeTime;
    }

    public String generateAccessToken(@Nonnull UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", roles);

        Date issuedDate = new Date();
        Date expirationDate = new Date(issuedDate.getTime() + accessTokenValidityTime.toMillis());
        return Jwts.builder()
                .claim("username", userDetails.getUsername())
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expirationDate)
                .signWith(accessKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(@Nonnull UserDetails userDetails) {
        Date issuedDate = new Date();
        Date expirationDate = new Date(issuedDate.getTime() + refreshTokenValidityTime.toMillis());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .expiration(expirationDate)
                .signWith(refreshKey, Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSignInKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isAccessTokenValid(@Nonnull String accessToken) {
        return validateToken(accessToken, accessKey);
    }

    public boolean isRefreshTokenValid(@Nonnull String refreshToken) {
        return validateToken(refreshToken, refreshKey);
    }

    private boolean validateToken(@Nonnull String token, @Nonnull SecretKey key) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(@Nonnull String token, @Nonnull SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(@Nonnull String token,@Nonnull String type) {
        switch (type) {
            case "REFRESH":
                return getClaims(token, refreshKey).getSubject();
            default:
                return getClaims(token, accessKey).getSubject();
        }
    }


}







