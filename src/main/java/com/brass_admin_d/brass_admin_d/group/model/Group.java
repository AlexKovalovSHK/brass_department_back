package com.brass_admin_d.brass_admin_d.group.model;

import com.brass_admin_d.brass_admin_d.session.model.SessionsGlobal;
import com.brass_admin_d.brass_admin_d.student.model.Student;
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
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_number", nullable = false)
    private String groupNumber;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private SessionsGlobal session;

    @OneToMany(mappedBy = "group")
    private Set<Student> students = new HashSet<>();
}