package com.brass_admin_d.brass_admin_d.exam.dao;

import com.brass_admin_d.brass_admin_d.exam.model.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    // Найти все результаты для конкретного студента
    List<ExamResult> findByStudentId(Long studentId);

    // Найти все результаты по конкретному экзамену
    List<ExamResult> findByExamId(Long examId);

    // Найти конкретный результат для студента по экзамену (очень полезный метод)
    Optional<ExamResult> findByStudentIdAndExamId(Long studentId, Long examId);
}