package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "video_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_progress_user_course",
                        columnNames = {"user_seq", "member_type", "course_id"}
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
    @Column(name = "video_progress_id")
    private Long videoProgressId;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    @Column(name = "member_type", nullable = false, length = 20)
    private String memberType; // MEMBER / SOCIAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private VideoCourse course;

    @Column(name = "final_completed_yn", nullable = false, length = 1)
    private String finalCompletedYn;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (finalCompletedYn == null) finalCompletedYn = "N";

        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}