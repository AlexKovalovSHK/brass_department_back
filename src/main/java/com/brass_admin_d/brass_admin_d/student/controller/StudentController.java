package com.brass_admin_d.brass_admin_d.student.controller;

import com.brass_admin_d.brass_admin_d.student.dto.NewStudentDto;
import com.brass_admin_d.brass_admin_d.student.dto.StudentDto;
import com.brass_admin_d.brass_admin_d.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Студенты", description = "Операции над студентами: создание, удаление, фильтрация")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Добавить нового студента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студент успешно добавлен")
    })
    @PostMapping
    public ResponseEntity<StudentDto> addStudent(@RequestBody NewStudentDto studentDto) {
        StudentDto createdStudent = studentService.addStudent(studentDto);
        return ResponseEntity.ok(createdStudent);
    }

    @Operation(summary = "Получить студента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студент найден"),
            @ApiResponse(responseCode = "404", description = "Студент не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Удалить студента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студент удалён")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> removeStudent(@PathVariable Long id) {
        boolean result = studentService.removeStudentById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить список всех студентов")
    @ApiResponse(responseCode = "200", description = "Список получен")
    @GetMapping
    public ResponseEntity<Iterable<StudentDto>> getAllStudents() {
        Iterable<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Получить студентов по ID группы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студенты найдены")
    })
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Iterable<StudentDto>> getStudentsByGroup(@PathVariable Long groupId) {
        Iterable<StudentDto> students = studentService.getStudentsByGroupId(groupId);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Получить студентов по ID сессии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студенты найдены")
    })
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<Iterable<StudentDto>> getStudentsBySession(@PathVariable Long sessionId) {
        Iterable<StudentDto> students = studentService.getStudentsBySessionId(sessionId);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Получить студентов на локальной сессии в глобальной сессии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студенты найдены")
    })
    @GetMapping("/sessions/{sessionId}/locals/{num}")
    public Iterable<StudentDto> getStudentsBySessionLocal(@PathVariable Long sessionId, @PathVariable Integer num){
        return studentService.getStudentsBySessionLocal(sessionId, num);
    }

    @Operation(summary = "Получить студентов по ID департамента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студенты найдены")
    })
    @GetMapping("/departments/{departmentId}")
    public ResponseEntity<Iterable<StudentDto>> getStudentsByDepartment(@PathVariable Long departmentId) {
        Iterable<StudentDto> students = studentService.getStudentsByDepartmentId(departmentId);
        return ResponseEntity.ok(students);
    }
}
