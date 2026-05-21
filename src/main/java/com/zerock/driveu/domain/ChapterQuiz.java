package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chapter_quiz",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chapter_quiz_order",
                        columnNames = {"chapter_id", "quiz_order"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private VideoChapter chapter;

    @Column(name = "quiz_order", nullable = false)
    private Integer quizOrder;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "quiz_type", nullable = false, length = 20)
    private String quizType;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "pass_required_yn", nullable = false, length = 1)
    private String passRequiredYn;

    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.quizOrder == null) {
            this.quizOrder = 1;
        }

        if (this.quizType == null) {
            this.quizType = "MULTIPLE";
        }

        if (this.passRequiredYn == null) {
            this.passRequiredYn = "Y";
        }

        if (this.useYn == null) {
            this.useYn = "Y";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}