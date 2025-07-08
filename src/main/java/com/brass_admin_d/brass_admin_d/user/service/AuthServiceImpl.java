package com.brass_admin_d.brass_admin_d.user.service;


import com.brass_admin_d.brass_admin_d.security.service.TokenService;
import com.brass_admin_d.brass_admin_d.security.utils.UserDetailsServiceImpl;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsServiceImpl userDetailsService;
    private final TokenService tokenService;
    @Value("${jwt.refresh-token.lifetime}")
    private Duration refreshTokenLifetime;
    @Value("${jwt.access-token.lifetime}")
    private Duration accessTokenLifetime;

    @Override
    public String generateAccessToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return tokenService.generateAccessToken(userDetails);
    }

    @Override
    public String generateRefreshToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return tokenService.generateRefreshToken(userDetails);
    }

    @Override
    public ResponseCookie createAccessTokenCookie(String username) {
        String accessToken = generateAccessToken(username);

        return ResponseCookie.from("access-token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessTokenLifetime) // Можно передавать Duration напрямую
                .sameSite("None")
                .build();
    }

    // Изменяем возвращаемый тип с Cookie на ResponseCookie
    @Override
    public ResponseCookie createRefreshTokenCookie(String username) { // Я переименовал его для соответствия с контроллером
        String refreshToken = generateRefreshToken(username);

        return ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenLifetime) // Можно передавать Duration напрямую
                .sameSite("None")
                .build();
    }



}
