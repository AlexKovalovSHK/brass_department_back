package com.brass_admin_d.brass_admin_d.exam.dto;

import com.brass_admin_d.brass_admin_d.exam.model.Exam;
import com.brass_admin_d.brass_admin_d.exam.model.ExamStatus;
import com.brass_admin_d.brass_admin_d.student.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamResultDto {
    private Long id;
    private Long examId;
    private String examName;
    private String examNameRu;
    private Long studentId;
    private Double score;
    private String teachersName;
    private String teachersNameRu;
    private String examStatus;


}
