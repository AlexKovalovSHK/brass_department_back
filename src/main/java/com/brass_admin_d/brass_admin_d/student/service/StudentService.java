package com.brass_admin_d.brass_admin_d.student.service;

import com.brass_admin_d.brass_admin_d.exam.dto.ExamDto;
import com.brass_admin_d.brass_admin_d.student.dto.NewStudentDto;
import com.brass_admin_d.brass_admin_d.student.dto.StudentDto;

public interface StudentService {
    StudentDto addStudent(NewStudentDto studentDto);
    StudentDto getStudentById(Long id);
    Boolean removeStudentById(Long id);

    Iterable<StudentDto> getAllStudents();
    Iterable<StudentDto> getStudentsByGroupId(Long id);
    Iterable<StudentDto> getStudentsBySessionId(Long id);
    Iterable<StudentDto> getStudentsBySessionLocal(Long globalSessionId, Integer num); // getStudentsBySessionIdAndGroupId
    Iterable<StudentDto> getStudentsByDepartmentId(Long id);

}
