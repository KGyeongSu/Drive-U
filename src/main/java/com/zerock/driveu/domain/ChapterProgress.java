package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chapter_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chapter_progress_user_chapter",
                        columnNames = {"user_seq", "member_type", "chapter_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_progress_id")
    private Long chapterProgressId;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    @Column(name = "member_type", nullable = false, length = 20)
    private String memberType; // MEMBER / SOCIAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private VideoChapter chapter;

    @Column(name = "watched_sec", nullable = false)
    private Integer watchedSec;

    @Column(name = "max_watched_sec", nullable = false)
    private Integer maxWatchedSec;

    @Column(name = "completed_yn", nullable = false, length = 1)
    private String completedYn;

    @Column(name = "quiz_passed_yn", nullable = false, length = 1)
    private String quizPassedYn;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (watchedSec == null) watchedSec = 0;
        if (maxWatchedSec == null) maxWatchedSec = 0;
        if (completedYn == null) completedYn = "N";
        if (quizPassedYn == null) quizPassedYn = "N";

        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}