package com.brass_admin_d.brass_admin_d.exam.dto;

import com.brass_admin_d.brass_admin_d.exam.model.ExamResult;
import com.brass_admin_d.brass_admin_d.session.dto.GlobalSessionsDto;
import com.brass_admin_d.brass_admin_d.session.model.SessionsGlobal;
import com.brass_admin_d.brass_admin_d.subjects.dto.SubjectDto;
import com.brass_admin_d.brass_admin_d.subjects.model.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamDto {
    private Long id;
    private LocalDate examDate;
    private String examName;
    private String examNameRu;
    private Integer sessionNum;//local
    private Long subjectId;
    private Long sessionId;
}
