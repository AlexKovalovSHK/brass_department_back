package com.brass_admin_d.brass_admin_d.subjects.dao;

import com.brass_admin_d.brass_admin_d.subjects.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);
    // Новый метод для поиска по ID департамента
    List<Subject> findByDepartmentId(Long departmentId);

    // Новый метод для поиска по ID сессии через таблицу экзаменов
    @Query("SELECT DISTINCT s FROM Subject s JOIN s.exams e WHERE e.session.id = :sessionId")
    List<Subject> findBySessionId(@Param("sessionId") Long sessionId);
}