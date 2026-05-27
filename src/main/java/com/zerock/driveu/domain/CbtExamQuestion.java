package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "cbt_exam_question",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cbt_exam_question_order",
                        columnNames = {"exam_id", "question_order"}
                ),
                @UniqueConstraint(
                        name = "uk_cbt_exam_question_question",
                        columnNames = {"exam_id", "question_id"}
                )
        }
)

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examQuestionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private CbtExam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private CbtQuestion question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(nullable = false)
    private Integer scorePerQuestion;
}