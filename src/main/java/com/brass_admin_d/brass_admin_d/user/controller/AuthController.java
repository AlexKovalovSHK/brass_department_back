package com.brass_admin_d.brass_admin_d.user.controller;

import com.brass_admin_d.brass_admin_d.user.dto.NewUserDto;
import com.brass_admin_d.brass_admin_d.user.dto.UserDto;
import com.brass_admin_d.brass_admin_d.user.dto.UserLoginRequestDto;
import com.brass_admin_d.brass_admin_d.user.service.AuthService;
import com.brass_admin_d.brass_admin_d.user.service.UserAccountService;
import com.brass_admin_d.brass_admin_d.utils.MessageResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAccountService userAccountService;
    private final AuthService authService;

    @PostMapping("/registration")
    public UserDto registerUser(@RequestBody NewUserDto data, HttpServletResponse response) {
        return userAccountService.createUser(data);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody UserLoginRequestDto data, HttpServletResponse response) {
        UserDto userResponseDto = userAccountService.loginUser(data);
        response.addHeader(HttpHeaders.SET_COOKIE, authService.createAccessTokenCookie(userResponseDto.getUsername()).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, authService.createRefreshTokenCookie(userResponseDto.getUsername()).toString());
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(HttpServletResponse response) {
        userAccountService.logoutUser().forEach(cookie ->
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        );
        return ResponseEntity.ok(new MessageResponseDto("Successful logout"));
    }

}