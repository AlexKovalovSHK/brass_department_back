package com.brass_admin_d.brass_admin_d.user.controller;

import com.brass_admin_d.brass_admin_d.user.dto.ChangeRoleResponseDto;
import com.brass_admin_d.brass_admin_d.user.dto.UserDto;
import com.brass_admin_d.brass_admin_d.user.dto.exceptions.UserUnauthorizedException;
import com.brass_admin_d.brass_admin_d.user.model.User;
import com.brass_admin_d.brass_admin_d.user.service.UserAccountService;
import com.brass_admin_d.brass_admin_d.utils.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции с аккаунтами пользователей")
public class UserController {

    private final UserAccountService userAccountService;

    @Operation(summary = "Получить список всех пользователей с пагинацией")
    @GetMapping
    public Iterable<UserDto> getAllUsers(
            @RequestParam(defaultValue = "25") Integer size,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        return userAccountService.findAll(PageRequest.of(page, size));
    }

    @Operation(summary = "Получить текущего авторизованного пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new UserUnauthorizedException();
        }
        return userAccountService.findUserById(user.getId());
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{userId}")
    public UserDto findUserById(
            @Parameter(description = "ID пользователя") @PathVariable Integer userId) {
        return userAccountService.findUserById(userId);
    }

    @Operation(summary = "Удалить пользователя по ID")
    @DeleteMapping("/{userId}")
    public UserDto deleteUser(
            @Parameter(description = "ID пользователя") @PathVariable Integer userId) {
        return userAccountService.deleteUserById(userId);
    }

    @Operation(summary = "Изменить пароль пользователя")
    @PutMapping("/{userId}/password")
    public ResponseEntity<MessageResponseDto> changePassword(
            @Parameter(description = "ID пользователя") @PathVariable Integer userId,
            @Parameter(description = "Новый пароль в заголовке запроса")
            @RequestHeader("X-Password") String password) {
        userAccountService.changePassword(userId, password);
        return ResponseEntity.ok(new MessageResponseDto("Successfully changed password"));
    }

    @Operation(summary = "Загрузить аватар пользователя")
    @PostMapping("/{userId}/avatar")
    public ResponseEntity<Void> uploadAvatar(
            @Parameter(description = "Файл изображения") @RequestParam("file") MultipartFile file,
            @PathVariable Integer userId) {
        userAccountService.uploadAvatar(userId, file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить аватар пользователя")
    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<Void> removeAvatar(@PathVariable Integer userId) {
        userAccountService.removeAvatar(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавить роль пользователю")
    @PutMapping("/{userId}/role/{role}")
    public ChangeRoleResponseDto addRole(
            @PathVariable String userId,
            @PathVariable String role) {
        try {
            int id = Integer.parseInt(userId);
            return userAccountService.changeRoleList(id, role, true);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect user ID format");
        }
    }

    @Operation(summary = "Удалить роль у пользователя")
    @DeleteMapping("/{userId}/role/{role}")
    public ChangeRoleResponseDto removeRole(
            @PathVariable String userId,
            @PathVariable String role) {
        try {
            int id = Integer.parseInt(userId);
            return userAccountService.changeRoleList(id, role, false);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect user ID format");
        }
    }
}
