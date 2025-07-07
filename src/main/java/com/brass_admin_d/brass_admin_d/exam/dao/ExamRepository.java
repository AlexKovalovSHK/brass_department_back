package com.brass_admin_d.brass_admin_d.exam.dao;

import com.brass_admin_d.brass_admin_d.exam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findBySessionId(Long sessionId);

    // Поиск по сессии и предмету (очень частый кейс)
    List<Exam> findBySessionIdAndSubjectId(Long sessionId, Long subjectId);
    //List<Exam> findBySessionAndSubjectAndSessionNum(Long sessionId, Long subjectId, Integer sessionNum);

    // Проверка на дубликат
    Optional<Exam> findBySessionIdAndSubjectIdAndSessionNumAndExamName(Long sessionId, Long subjectId, Integer sessionNum, String examName);

    // Поиск экзаменов, которые сдавала определенная группа в рамках сессии
    @Query("SELECT e FROM Exam e " +
            "JOIN e.results res " +
            "JOIN res.student s " +
            "WHERE e.session.id = :sessionId AND s.group.id = :groupId")
    List<Exam> findBySessionIdAndGroupId(@Param("sessionId") Long sessionId, @Param("groupId") Long groupId);
}