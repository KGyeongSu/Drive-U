package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "video_course",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_course_type_order",
                        columnNames = {"course_type", "course_order"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_type", nullable = false, length = 20)
    private String courseType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "duration_sec", nullable = false)
    private Integer durationSec;

    @Column(name = "course_order", nullable = false)
    private Integer courseOrder;

    @Column(name = "required_yn", nullable = false, length = 1)
    private String requiredYn;

    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "seq")
    private Member createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.requiredYn == null) {
            this.requiredYn = "N";
        }

        if (this.useYn == null) {
            this.useYn = "Y";
        }

        if (this.courseOrder == null) {
            this.courseOrder = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLearningVideo (
            String category,
            String title,
            String description,
            String useYn
    ){
        this.category = category;
        this.title = title;
        this.description = description;
        this.useYn = useYn;
    }
}