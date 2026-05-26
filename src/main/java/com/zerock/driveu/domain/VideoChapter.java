package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "video_chapter",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_chapter_course_order",
                        columnNames = {"course_id", "chapter_order"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoChapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Long chapterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private VideoCourse course;

    @Column(name = "chapter_order", nullable = false)
    private Integer chapterOrder;

    @Column(name = "chapter_title", nullable = false, length = 200)
    private String chapterTitle;

    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;

    @Column(name = "start_sec", nullable = false)
    private Integer startSec;

    @Column(name = "end_sec", nullable = false)
    private Integer endSec;

    @Column(name = "quiz_required_yn", nullable = false, length = 1)
    private String quizRequiredYn;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.chapterOrder == null) {
            this.chapterOrder = 1;
        }

        if (this.quizRequiredYn == null) {
            this.quizRequiredYn = "N";
        }

        if (this.startSec == null) {
            this.startSec = 0;
        }

        if (this.endSec == null) {
            this.endSec = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}