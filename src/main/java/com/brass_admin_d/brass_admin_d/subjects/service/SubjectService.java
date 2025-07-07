package com.brass_admin_d.brass_admin_d.subjects.service;

import com.brass_admin_d.brass_admin_d.subjects.dto.NewSubjectDto;
import com.brass_admin_d.brass_admin_d.subjects.dto.SubjectDto;

public interface SubjectService {
    SubjectDto addSubject(NewSubjectDto subjectDto);
    SubjectDto getSubjectById(Long id);
    SubjectDto updateSubjectById(SubjectDto subjectDto);
    Boolean deleteSubjectById(Long id);

    Iterable<SubjectDto> getAllSubjects();
    Iterable<SubjectDto> getSubjectBySessionId(Long sessionId);
    Iterable<SubjectDto> getSubjectByDepartmentId(Long departmentId);
}
