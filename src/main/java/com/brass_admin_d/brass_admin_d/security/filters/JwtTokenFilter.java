package com.brass_admin_d.brass_admin_d.security.filters;


import com.brass_admin_d.brass_admin_d.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String refreshToken = getAccessTokenFromRequest(request, "refresh-token");

            if (refreshToken != null && tokenService.isRefreshTokenValid(refreshToken)) {

                String accessToken = getAccessTokenFromRequest(request, "access-token");

                UserDetails userDetails = null;

                if (accessToken != null && tokenService.isAccessTokenValid(accessToken)) {
                    userDetails = userDetailsService.loadUserByUsername(tokenService.getUsernameFromToken(accessToken, "ACCESS"));
                } else {
                    userDetails = userDetailsService.loadUserByUsername(tokenService.getUsernameFromToken(refreshToken, "REFRESH"));
                    String newAccessToken = tokenService.generateAccessToken(userDetails);
                    Cookie cookie = new Cookie("access-token", newAccessToken);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (RuntimeException e) {
            List<Cookie> cookies = new ArrayList<>();
            cookies.add(resetCookie("access-token"));
            cookies.add(resetCookie("refresh-token"));
            cookies.forEach(response::addCookie);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private Cookie resetCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }
}
