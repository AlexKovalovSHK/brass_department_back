package com.brass_admin_d.brass_admin_d.user.service;


import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

public interface AuthService {

    String generateAccessToken(String username);

    String generateRefreshToken(String username);

    ResponseCookie createAccessTokenCookie(String username);
    ResponseCookie createRefreshTokenCookie(String username);
}