package com.brass_admin_d.brass_admin_d.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewExamDto {
    private String examName;
    private String examNameRu;
    private Integer sessionNum;//local
    private Long subjectId;
    private Long sessionId;
}
