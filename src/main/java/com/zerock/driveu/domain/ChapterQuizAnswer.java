package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapter_quiz_answer")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterQuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_answer_id")
    private Long quizAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_submission_id", nullable = false)
    private ChapterQuizSubmission quizSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private ChapterQuiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_choice_id", nullable = false)
    private ChapterQuizChoice selectedChoice;

    @Column(name = "correct_yn", nullable = false, length = 1)
    private String correctYn;

    @PrePersist
    public void prePersist() {
        if (this.correctYn == null) {
            this.correctYn = "N";
        }
    }
}