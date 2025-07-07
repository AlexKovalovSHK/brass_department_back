package com.brass_admin_d.brass_admin_d.subjects.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewSubjectDto {
    private String name;
    private String nameRu;
    private Long departmentId;
}
