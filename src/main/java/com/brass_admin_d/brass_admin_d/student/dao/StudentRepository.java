package com.brass_admin_d.brass_admin_d.student.dao;

import com.brass_admin_d.brass_admin_d.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Поиск студентов по части фамилии (без учета регистра)
    List<Student> findByLastNameContainingIgnoreCase(String lastName);

    // Поиск студента по его учетной записи User
    Optional<Student> findByUserId(Integer userId);

    Optional<Student> findByNumberBook(String numberBook);
    List<Student> findByGroupId(Long groupId);

    @Query("SELECT s FROM Student s WHERE s.group.session.id = :sessionId")
    List<Student> findBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT s FROM Student s WHERE s.group.session.department.id = :departmentId")
    List<Student> findByDepartmentId(@Param("departmentId") Long departmentId);

    List<Student> findByGroup_Session_IdAndSessionNum(Long sessionId, Integer sessionNum);
}