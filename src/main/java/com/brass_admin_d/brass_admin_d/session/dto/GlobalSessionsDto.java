package com.brass_admin_d.brass_admin_d.session.dto;

import com.brass_admin_d.brass_admin_d.department.dto.DepartmentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalSessionsDto {
    private Long id;
    private LocalDate sessionDate;
    private String city;
    private DepartmentDto department;
    /*private List<GroupDto> groups;
    private List<ExamDto> exams;*/
}
