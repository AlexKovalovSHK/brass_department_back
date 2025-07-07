package com.brass_admin_d.brass_admin_d.group.controller;

import com.brass_admin_d.brass_admin_d.group.dto.GroupDto;
import com.brass_admin_d.brass_admin_d.group.dto.NewGroupDto;
import com.brass_admin_d.brass_admin_d.group.service.GroupService;
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
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Группы", description = "Управление группами внутри сессий")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "Создать группу", description = "Создает новую группу, связанную с существующей сессией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Группа успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка создания, сессия не найдена")
    })
    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody NewGroupDto newGroupDto) {
        try {
            GroupDto createdGroup = groupService.createGroup(newGroupDto);
            return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Получить группу по ID", description = "Возвращает данные группы по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Группа найдена"),
            @ApiResponse(responseCode = "404", description = "Группа не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id) {
        try {
            GroupDto group = groupService.getGroupById(id);
            return ResponseEntity.ok(group);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить группу", description = "Удаляет группу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Группа успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Группа не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        boolean deleted = groupService.deleteGroupById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить все группы", description = "Возвращает список всех групп")
    @ApiResponse(responseCode = "200", description = "Список успешно получен")
    @GetMapping
    public ResponseEntity<Iterable<GroupDto>> getAllGroups() {
        Iterable<GroupDto> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Получить группы по ID сессии", description = "Возвращает список групп, связанных с указанной сессией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Группы найдены"),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Iterable<GroupDto>> getGroupsBySessionId(@PathVariable Long sessionId) {
        try {
            Iterable<GroupDto> groups = groupService.getGroupBySessionId(sessionId);
            return ResponseEntity.ok(groups);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
