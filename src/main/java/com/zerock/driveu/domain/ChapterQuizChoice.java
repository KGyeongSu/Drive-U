package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "chapter_quiz_choice",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chapter_quiz_choice_order",
                        columnNames = {"quiz_id", "choice_order"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterQuizChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long choiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private ChapterQuiz quiz;

    @Column(name = "choice_order", nullable = false)
    private Integer choiceOrder;

    @Column(name = "choice_text", nullable = false, length = 500)
    private String choiceText;

    @Column(name = "correct_yn", nullable = false, length = 1)
    private String correctYn;

    @PrePersist
    public void prePersist() {
        if (this.choiceOrder == null) {
            this.choiceOrder = 1;
        }

        if (this.correctYn == null) {
            this.correctYn = "N";
        }
    }
}