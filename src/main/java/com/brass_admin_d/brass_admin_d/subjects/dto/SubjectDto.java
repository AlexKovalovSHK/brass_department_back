package com.brass_admin_d.brass_admin_d.subjects.dto;

import com.brass_admin_d.brass_admin_d.department.model.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDto {
    private Long id;
    private String name;
    private String nameRu;
    private Long departmentId;
}
