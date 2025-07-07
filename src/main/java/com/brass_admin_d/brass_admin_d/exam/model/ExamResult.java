package com.brass_admin_d.brass_admin_d.exam.model;

import com.brass_admin_d.brass_admin_d.student.model.Student;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "exam_results")
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    private Double score;

    private String teachersName;

    private String teachersNameRu;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_status")
    private ExamStatus examStatus;
}