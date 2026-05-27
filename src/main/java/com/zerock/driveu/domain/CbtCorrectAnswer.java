package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "cbt_correct_answer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cbt_correct_answer_question_no",
                        columnNames = {"question_id", "correct_choice_no"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtCorrectAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long correctAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private CbtQuestion question;

    @Column(nullable = false)
    private Integer correctChoiceNo;
}