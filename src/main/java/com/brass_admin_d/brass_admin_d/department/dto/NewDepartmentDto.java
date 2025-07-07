package com.brass_admin_d.brass_admin_d.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewDepartmentDto {
    private String departName;
    private LocalDate creationDate = LocalDate.now();
}
