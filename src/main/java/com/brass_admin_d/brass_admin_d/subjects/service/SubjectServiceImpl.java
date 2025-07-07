package com.brass_admin_d.brass_admin_d.subjects.service;

import com.brass_admin_d.brass_admin_d.department.dto.DepartmentDto;
import com.brass_admin_d.brass_admin_d.department.model.Department;
import com.brass_admin_d.brass_admin_d.department.dao.DepartmentRepository;
import com.brass_admin_d.brass_admin_d.subjects.dao.SubjectRepository;
import com.brass_admin_d.brass_admin_d.subjects.dto.NewSubjectDto;
import com.brass_admin_d.brass_admin_d.subjects.dto.SubjectDto;
import com.brass_admin_d.brass_admin_d.subjects.model.Subject;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public SubjectDto addSubject(NewSubjectDto subjectDto) {
        Department department = departmentRepository.findById(subjectDto.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + subjectDto.getDepartmentId()));

        Subject newSubject = new Subject();
        newSubject.setName(subjectDto.getName());
        newSubject.setNameRu(subjectDto.getNameRu());
        newSubject.setDepartment(department);

        Subject savedSubject = subjectRepository.save(newSubject);
        return subjectToDto(savedSubject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
        return subjectToDto(subject);
    }

    @Override
    @Transactional
    public SubjectDto updateSubjectById(SubjectDto subjectDto) {
        Subject subjectToUpdate = subjectRepository.findById(subjectDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + subjectDto.getId()));

        // Обновляем департамент, если ID изменился
        if (!subjectToUpdate.getDepartment().getId().equals(subjectDto.getDepartmentId())) {
            Department newDepartment = departmentRepository.findById(subjectDto.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("New department not found with id: " + subjectDto.getDepartmentId()));
            subjectToUpdate.setDepartment(newDepartment);
        }

        subjectToUpdate.setName(subjectDto.getName());

        Subject updatedSubject = subjectRepository.save(subjectToUpdate);
        return subjectToDto(updatedSubject);
    }

    @Override
    @Transactional
    public Boolean deleteSubjectById(Long id) {
        if (!subjectRepository.existsById(id)) {
            return false;
        }
        subjectRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::subjectToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjectBySessionId(Long sessionId) {
        return subjectRepository.findBySessionId(sessionId).stream()
                .map(this::subjectToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjectByDepartmentId(Long departmentId) {
        return subjectRepository.findByDepartmentId(departmentId).stream()
                .map(this::subjectToDto)
                .collect(Collectors.toList());
    }

    // =================================================================
    // ПРИВАТНЫЕ МЕТОДЫ-ХЕЛПЕРЫ ДЛЯ ПРЕОБРАЗОВАНИЯ В DTO
    // =================================================================

    private SubjectDto subjectToDto(Subject subject) {
        if (subject == null) {
            return null;
        }

        SubjectDto dto = new SubjectDto();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setNameRu(subject.getNameRu());
        dto.setDepartmentId(subject.getDepartment().getId());
        return dto;
    }

    private DepartmentDto departmentToInfoDto(Department department) {
        if (department == null) {
            return null;
        }

        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setDepartName(department.getDepartName());
        return dto;
    }
}