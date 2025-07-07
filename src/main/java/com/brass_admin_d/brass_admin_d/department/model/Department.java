package com.brass_admin_d.brass_admin_d.department.model;

import com.brass_admin_d.brass_admin_d.session.model.SessionsGlobal;
import com.brass_admin_d.brass_admin_d.user.model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "depart_name", nullable = false)
    private String departName;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionsGlobal> globalSessions = new ArrayList<>();

    @OneToMany(
            mappedBy = "department", // (1)
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, // (2)
            orphanRemoval = false // (3)
    )
    @JsonManagedReference // (4) Опционально, но рекомендуется
    private List<User> users = new ArrayList<>();

    // Вспомогательные методы для синхронизации
    public void addUser(User user) {
        this.users.add(user);
        user.setDepartment(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.setDepartment(null);
    }

}
