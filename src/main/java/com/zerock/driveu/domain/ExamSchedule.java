package com.zerock.driveu.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "exam_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(nullable = false, length = 30)
    private String examType;            // 학과,기능,도로주행 종류

    @Column(length = 30)
    private String licenseType;         // 면허종류 (학과/도로:null, 기능만 구분)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_center_id", nullable = false)
    private TestCenter testCenter;      // ex)대전xxx시험장

    @Column(nullable = false)
    private LocalDate examDate;         // 시험날짜

    @Column(nullable = false)
    private LocalTime examTime;         // 시험시간

    @Column(nullable = false)
    private int maxCount;               // 총 정원

    @Column(nullable = false)
    private int currentCount;           // 현재 정원

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;    // 데이터가 언제 만들어졌는지

    @Column(nullable = false)
    private LocalDateTime updatedAt;    // 언제 마지막으로 수정됐는지

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}


