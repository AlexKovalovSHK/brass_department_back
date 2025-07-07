package com.brass_admin_d.brass_admin_d.exam.service;

import com.brass_admin_d.brass_admin_d.exam.dto.ExamResultDto;
import com.brass_admin_d.brass_admin_d.exam.dto.ScoreDto;
import com.brass_admin_d.brass_admin_d.exam.dto.ExamDto;
import com.brass_admin_d.brass_admin_d.exam.dto.NewExamDto;
import com.brass_admin_d.brass_admin_d.exam.model.ExamResult;

public interface ExamService {
    ExamDto addExam(NewExamDto newExamDto);
    ExamDto getExamById(Long id);
    Boolean deleteExamById(Long id);

    Iterable<ExamDto> getAllExams();
    Iterable<ExamDto> getExamsBySessionId(Long id);
    Iterable<ExamDto> getExamsBySessionIdAndSubjectId(Long sessionId, Long subjectId);
    Iterable<ExamDto> getExamsBySessionIdAndGroupId(Long sessionId, Long groupId);

    ExamResultDto updateResult(ScoreDto dto);
}
