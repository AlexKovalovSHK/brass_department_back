package com.brass_admin_d.brass_admin_d.user.service;

import com.brass_admin_d.brass_admin_d.user.dto.ChangeRoleResponseDto;
import com.brass_admin_d.brass_admin_d.user.dto.NewUserDto;
import com.brass_admin_d.brass_admin_d.user.dto.UserDto;
import com.brass_admin_d.brass_admin_d.user.dto.UserLoginRequestDto;
import org.springframework.data.domain.PageRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserAccountService {

    UserDto loginUser(UserLoginRequestDto data);

    List<ResponseCookie> logoutUser();

    UserDto findUserByUsername(String username);

    UserDto createUser(NewUserDto user);
    UserDto findUserById(Long userId);

    UserDto deleteUserById(Long userId);

    Iterable<UserDto> findAll(PageRequest pageRequest);

    ChangeRoleResponseDto changeRoleList(Long userId, String roleName, Boolean isAddRole);

    void changePassword(Long userId, String password);

    void uploadAvatar(Long userId, MultipartFile file);

    void removeAvatar(Long userId);

}
