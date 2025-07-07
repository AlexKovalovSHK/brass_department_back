package com.brass_admin_d.brass_admin_d.student.dto;

import com.brass_admin_d.brass_admin_d.group.model.Group;
import com.brass_admin_d.brass_admin_d.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String firstNameRu;
    private String lastNameRu;
    private Integer sessionNum;
    private LocalDate dateBirth;
    private LocalDate dateBaptism;
    private LocalDate dateReceipt;
    private LocalDate dateEnd;
    private String tel;
    private String instrument;
    private String speciality;
    private String email;
    private String numberBook;
    private String note;
    private String groupNum;
}
