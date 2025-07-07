package com.brass_admin_d.brass_admin_d.student.model;

import com.brass_admin_d.brass_admin_d.exam.model.ExamResult;
import com.brass_admin_d.brass_admin_d.group.model.Group;
import com.brass_admin_d.brass_admin_d.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name_ru", nullable = false)
    private String firstNameRu;

    @Column(name = "last_name_ru", nullable = false)
    private String lastNameRu;

    @Column(name = "city")
    private  String city;

    @Column(name = "session_num")
    private Integer sessionNum;

    @Column(name = "date_birth")
    private LocalDate dateBirth;

    @Column(name = "date_baptism")
    private LocalDate dateBaptism;

    @Column(name = "date_receipt")
    private LocalDate dateReceipt;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    private String tel;
    private String instrument;
    private String speciality;
    private String email;

    @Column(name = "number_book")
    private String numberBook;

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExamResult> examResults = new HashSet<>();
}