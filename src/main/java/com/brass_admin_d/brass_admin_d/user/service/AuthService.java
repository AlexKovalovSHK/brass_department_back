package com.brass_admin_d.brass_admin_d.user.service;


import jakarta.servlet.http.Cookie;

public interface AuthService {

    String generateAccessToken(String username);

    String generateRefreshToken(String username);

    Cookie createAccessTokenCookie(String username);

    Cookie createRefreshToken(String username);
}