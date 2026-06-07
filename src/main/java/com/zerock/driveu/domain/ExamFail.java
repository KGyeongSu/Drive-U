package com.zerock.driveu.domain;

import com.zerock.driveu.domain.enums.ExamType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_fail")   // ← uniqueConstraints 없음: 같은 시험 여러 번 불합격 허용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExamFail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examFailId;

    @Column(nullable = false)
    private Long userSeq;
    @Column(nullable = false, length = 20)
    private String memberType;          // "MEMBER" / "SOCIAL"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamType examType;          // WRITTEN / FUNCTION / DRIVE

    @Column(nullable = false, length = 30)
    private String licenseType;         // "1종 보통", "2종 보통" 등

    // 불합격 점수 (기준 미달 점수도 기록)
    @Column(nullable = false)
    private Long score;

    // 합격 시점의 거울 → 불합격 '시점'
    @Column(nullable = false)
    private LocalDate failedDate;       // 관리자가 불합격 입력한 날

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.failedDate == null) {
            this.failedDate = LocalDate.now();
        }
    }
}