package com.brass_admin_d.brass_admin_d.session.controller;

import com.brass_admin_d.brass_admin_d.session.dto.GlobalSessionsDto;
import com.brass_admin_d.brass_admin_d.session.dto.NewGlobalSessionsDto;
import com.brass_admin_d.brass_admin_d.session.service.SessionsGlobalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Глобальные Сессии", description = "Управление глобальными сессиями пользователей")
public class SessionsGlobalController {

    private final SessionsGlobalService sessionsGlobalService;

    @Operation(summary = "Создать новую сессию", description = "Создает глобальную сессию для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Сессия успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка создания, департамент не найден")
    })
    @PostMapping
    public ResponseEntity<GlobalSessionsDto> createSession(@RequestBody NewGlobalSessionsDto requestDto) {
        try {
            GlobalSessionsDto createdSession = sessionsGlobalService.createSession(requestDto);
            return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Получить сессию по ID", description = "Возвращает данные сессии по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сессия найдена"),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GlobalSessionsDto> getSessionById(@PathVariable Long id) {
        try {
            GlobalSessionsDto session = sessionsGlobalService.getSessionById(id);
            return ResponseEntity.ok(session);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить список всех сессий", description = "Возвращает список всех активных или завершённых сессий")
    @ApiResponse(responseCode = "200", description = "Список успешно получен")
    @GetMapping
    public ResponseEntity<Iterable<GlobalSessionsDto>> getAllSessions() {
        Iterable<GlobalSessionsDto> sessions = sessionsGlobalService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "Обновить сессию", description = "Обновляет информацию о сессии по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сессия успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GlobalSessionsDto> updateSession(@PathVariable Long id, @RequestBody GlobalSessionsDto requestDto) {
        try {
            GlobalSessionsDto updatedSession = sessionsGlobalService.updateSession(id, requestDto);
            return ResponseEntity.ok(updatedSession);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить сессию", description = "Удаляет сессию по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сессия успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteSession(@PathVariable Long id) {
        try {
            Boolean result = sessionsGlobalService.deleteSession(id);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
