package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chapter_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chapter_progress_member_chapter",
                        columnNames = {"member_seq", "chapter_id"}
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq", nullable = false)
    private Member member;

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
        if (this.watchedSec == null) {
            this.watchedSec = 0;
        }

        if (this.maxWatchedSec == null) {
            this.maxWatchedSec = 0;
        }

        if (this.completedYn == null) {
            this.completedYn = "N";
        }

        if (this.quizPassedYn == null) {
            this.quizPassedYn = "N";
        }

        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}