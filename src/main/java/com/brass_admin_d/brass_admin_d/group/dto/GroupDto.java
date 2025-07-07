package com.brass_admin_d.brass_admin_d.group.dto;

import com.brass_admin_d.brass_admin_d.session.model.SessionsGlobal;
import com.brass_admin_d.brass_admin_d.student.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
    private Long id;
    private String groupNumber;
    private LocalDate creationDate;
    private Long sessionId;
    //private List<Student> students;
}
