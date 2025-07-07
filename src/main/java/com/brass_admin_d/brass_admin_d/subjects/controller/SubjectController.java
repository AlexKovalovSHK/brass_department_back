package com.brass_admin_d.brass_admin_d.subjects.controller;

import com.brass_admin_d.brass_admin_d.subjects.dto.NewSubjectDto;
import com.brass_admin_d.brass_admin_d.subjects.dto.SubjectDto;
import com.brass_admin_d.brass_admin_d.subjects.service.SubjectService;
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
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Tag(name = "Предметы", description = "Управление предметами: создание, просмотр, обновление и удаление")
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(summary = "Получить все предметы")
    @ApiResponse(responseCode = "200", description = "Список предметов успешно получен")
    @GetMapping
    public ResponseEntity<Iterable<SubjectDto>> getAllSubjects() {
        Iterable<SubjectDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @Operation(summary = "Получить предмет по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Предмет найден"),
            @ApiResponse(responseCode = "404", description = "Предмет не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDto> getSubjectById(@PathVariable Long id) {
        try {
            SubjectDto subject = subjectService.getSubjectById(id);
            return ResponseEntity.ok(subject);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Создать новый предмет")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Предмет успешно создан")
    })
    @PostMapping
    public ResponseEntity<SubjectDto> addSubject(@RequestBody NewSubjectDto newSubjectDto) {
        SubjectDto createdSubject = subjectService.addSubject(newSubjectDto);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить предмет по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Предмет успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Предмет не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDto> updateSubject(@PathVariable Long id, @RequestBody SubjectDto subjectDto) {
        try {
            subjectDto.setId(id);
            SubjectDto updatedSubject = subjectService.updateSubjectById(subjectDto);
            return ResponseEntity.ok(updatedSubject);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить предмет по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Предмет успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Предмет не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        boolean deleted = subjectService.deleteSubjectById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Получить предметы по ID департамента")
    @ApiResponse(responseCode = "200", description = "Предметы найдены")
    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<Iterable<SubjectDto>> getSubjectsByDepartment(@PathVariable Long departmentId) {
        Iterable<SubjectDto> subjects = subjectService.getSubjectByDepartmentId(departmentId);
        return ResponseEntity.ok(subjects);
    }

    @Operation(summary = "Получить предметы по ID сессии")
    @ApiResponse(responseCode = "200", description = "Предметы найдены")
    @GetMapping("/by-session/{sessionId}")
    public ResponseEntity<Iterable<SubjectDto>> getSubjectsBySession(@PathVariable Long sessionId) {
        Iterable<SubjectDto> subjects = subjectService.getSubjectBySessionId(sessionId);
        return ResponseEntity.ok(subjects);
    }
}
