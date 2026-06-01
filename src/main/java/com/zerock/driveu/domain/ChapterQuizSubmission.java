package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_quiz_submission")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterQuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_submission_id")
    private Long quizSubmissionId;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    @Column(name = "member_type", nullable = false, length = 20)
    private String memberType; // MEMBER / SOCIAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private VideoChapter chapter;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;

    @Column(name = "wrong_count", nullable = false)
    private Integer wrongCount;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "pass_yn", nullable = false, length = 1)
    private String passYn;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    public void prePersist() {
        if (this.totalCount == null) {
            this.totalCount = 0;
        }

        if (this.correctCount == null) {
            this.correctCount = 0;
        }

        if (this.wrongCount == null) {
            this.wrongCount = 0;
        }

        if (this.score == null) {
            this.score = 0;
        }

        if (this.passYn == null) {
            this.passYn = "N";
        }

        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
    }
}