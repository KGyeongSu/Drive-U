package com.zerock.driveu.domain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cbt_exam")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examId;

    @Column(nullable = false, length = 200)
    private String examTitle;

    @Column(nullable = false, length = 50)
    private String licenseType;

    @Column(nullable = false)
    private Integer totalQuestionCount;

    @Column(nullable = false)
    private Integer timeLimitMin;

    @Column(nullable = false)
    private Integer passScore;

    @Column(nullable = false, length = 1)
    @Builder.Default
    private String activeYn = "Y";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.activeYn == null) {
            this.activeYn = "Y";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}