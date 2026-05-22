package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "video_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_progress_member_course",
                        columnNames = {"member_seq", "course_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private VideoCourse course;

    @Column(name = "watched_sec", nullable = false)
    private Integer watchedSec;

    @Column(name = "max_watched_sec", nullable = false)
    private Integer maxWatchedSec;

    @Column(name = "progress_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressRate;

    @Column(name = "completed_chapter_count", nullable = false)
    private Integer completedChapterCount;

    @Column(name = "total_chapter_count", nullable = false)
    private Integer totalChapterCount;

    @Column(name = "final_completed_yn", nullable = false, length = 1)
    private String finalCompletedYn;

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

        if (this.progressRate == null) {
            this.progressRate = BigDecimal.ZERO;
        }

        if (this.completedChapterCount == null) {
            this.completedChapterCount = 0;
        }

        if (this.totalChapterCount == null) {
            this.totalChapterCount = 0;
        }

        if (this.finalCompletedYn == null) {
            this.finalCompletedYn = "N";
        }

        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}