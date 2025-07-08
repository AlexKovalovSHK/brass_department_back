package com.brass_admin_d.brass_admin_d.department.controller;

import com.brass_admin_d.brass_admin_d.department.dto.DepartmentDto;
import com.brass_admin_d.brass_admin_d.department.dto.NewDepartmentDto;
import com.brass_admin_d.brass_admin_d.department.dto.UpdateDepartmentDto;
import com.brass_admin_d.brass_admin_d.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department API", description = "Операции с департаментами")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Создать департамент", description = "Создает новый департамент на основе переданных данных")
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody NewDepartmentDto newDepartmentDto) {
        DepartmentDto created = departmentService.createDepartment(newDepartmentDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить департамент по ID", description = "Возвращает департамент по указанному идентификатору")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        try {
            DepartmentDto department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(department);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @DeleteMapping("/{id}/isDeleted/{delete}")
    public ResponseEntity<Boolean> deleteDepartment(@PathVariable Long id, @PathVariable Boolean delete) {
        try {
            Boolean result = departmentService.deleteDepartment(id, delete);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    public  ResponseEntity<DepartmentDto> updateDepartment(@RequestBody UpdateDepartmentDto dto) {
        try {
            DepartmentDto result = departmentService.updateDepartment(dto);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
