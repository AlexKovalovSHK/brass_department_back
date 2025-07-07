package com.brass_admin_d.brass_admin_d.exam.controller;

import com.brass_admin_d.brass_admin_d.exam.dto.ExamDto;
import com.brass_admin_d.brass_admin_d.exam.dto.ExamResultDto;
import com.brass_admin_d.brass_admin_d.exam.dto.NewExamDto;
import com.brass_admin_d.brass_admin_d.exam.dto.ScoreDto;
import com.brass_admin_d.brass_admin_d.exam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
@Tag(name = "Экзамены", description = "Управление экзаменами и результатами экзаменов")
public class ExamController {

    private final ExamService examService;

    @Operation(summary = "Добавить новый экзамен")
    @ApiResponse(responseCode = "200", description = "Экзамен успешно добавлен")
    @PostMapping
    public ResponseEntity<ExamDto> addExam(@RequestBody NewExamDto newExamDto) {
        ExamDto createdExam = examService.addExam(newExamDto);
        return ResponseEntity.ok(createdExam);
    }

    @Operation(summary = "Получить экзамен по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Экзамен найден"),
            @ApiResponse(responseCode = "404", description = "Экзамен не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExamDto> getExamById(@PathVariable Long id) {
        ExamDto exam = examService.getExamById(id);
        return ResponseEntity.ok(exam);
    }

    @Operation(summary = "Удалить экзамен по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Экзамен удалён"),
            @ApiResponse(responseCode = "404", description = "Экзамен не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamById(@PathVariable Long id) {
        boolean deleted = examService.deleteExamById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Получить все экзамены")
    @ApiResponse(responseCode = "200", description = "Список экзаменов успешно получен")
    @GetMapping
    public ResponseEntity<Iterable<ExamDto>> getAllExams() {
        Iterable<ExamDto> exams = examService.getAllExams();
        return ResponseEntity.ok(exams);
    }

    @Operation(summary = "Получить экзамены по ID сессии")
    @ApiResponse(responseCode = "200", description = "Экзамены найдены")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Iterable<ExamDto>> getExamsBySessionId(@PathVariable Long sessionId) {
        Iterable<ExamDto> exams = examService.getExamsBySessionId(sessionId);
        return ResponseEntity.ok(exams);
    }

    @Operation(summary = "Получить экзамены по ID сессии и предмета")
    @ApiResponse(responseCode = "200", description = "Экзамены найдены")
    @GetMapping("/session/{sessionId}/subject/{subjectId}")
    public ResponseEntity<Iterable<ExamDto>> getExamsBySessionIdAndSubjectId(
            @PathVariable Long sessionId,
            @PathVariable Long subjectId) {
        Iterable<ExamDto> exams = examService.getExamsBySessionIdAndSubjectId(sessionId, subjectId);
        return ResponseEntity.ok(exams);
    }

    @Operation(summary = "Получить экзамены по ID сессии и группы")
    @ApiResponse(responseCode = "200", description = "Экзамены найдены")
    @GetMapping("/session/{sessionId}/group/{groupId}")
    public ResponseEntity<Iterable<ExamDto>> getExamsBySessionIdAndGroupId(
            @PathVariable Long sessionId,
            @PathVariable Long groupId) {
        Iterable<ExamDto> exams = examService.getExamsBySessionIdAndGroupId(sessionId, groupId);
        return ResponseEntity.ok(exams);
    }

    @Operation(summary = "Обновить результат экзамена для студента")
    @ApiResponse(responseCode = "200", description = "Результат успешно обновлён")
    @PutMapping("/results")
    public ExamResultDto updateResult(@RequestBody ScoreDto dto) {
        return examService.updateResult(dto);
    }
}
