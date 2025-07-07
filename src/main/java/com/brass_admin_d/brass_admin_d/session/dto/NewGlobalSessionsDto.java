package com.brass_admin_d.brass_admin_d.session.dto;

import com.brass_admin_d.brass_admin_d.department.model.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewGlobalSessionsDto {
    private Long departmentId;
    private String city;
}
