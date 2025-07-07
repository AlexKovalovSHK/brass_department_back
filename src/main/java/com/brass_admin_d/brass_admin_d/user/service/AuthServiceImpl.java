package com.brass_admin_d.brass_admin_d.user.service;


import com.brass_admin_d.brass_admin_d.security.service.TokenService;
import com.brass_admin_d.brass_admin_d.security.utils.UserDetailsServiceImpl;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    public Cookie createAccessTokenCookie(String username) {
        String accessToken = generateAccessToken(username);
        Cookie cookie = new Cookie("access-token", accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) accessTokenLifetime.getSeconds());
        return cookie;
    }

    @Override
    public Cookie createRefreshToken(String username) {
        String refreshToken = generateRefreshToken(username);
        Cookie cookie = new Cookie("refresh-token", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) refreshTokenLifetime.getSeconds());
        //cookie.setSecure(true); // TODO проверить на фронтенде при влюченной настройке
        return cookie;
    }
}
