package com.zerock.driveu.domain;

import com.zerock.driveu.domain.enums.ApplicationStatus;
import com.zerock.driveu.domain.enums.ExamType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    // 회원 FK
    @Column(nullable = false)
    private Long userSeq;

    @Column(nullable = false, length = 20)
    private String memberType;          // "MEMBER" / "SOCIAL"

    // 시험 일정 FK (객체 매핑, LAZY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ExamSchedule examSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamType examType;          // WRITTEN / SKILL / ROAD

    @Column(nullable = false, length = 30)
    private String licenseType;         // "1종 보통" 등

    @Column(nullable = false, length = 20)
    private String contactPhone;        // 신청건 전용 연락처

    @Column(nullable = false, length = 100)
    private String contactEmail;        // 신청건 전용 이메일

    @Column(nullable = false)
    private int fee;                    // 응시료 (스냅샷)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;   // WAITING_PAYMENT / COMPLETED / FAILED / CANCELLED

    @Column(nullable = false, length = 50, unique = true)
    private String merchantUid;         // 포트원 주문번호 (검증용)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

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

    // 상태 변경 메서드
    public void markAsCompleted() {
        this.status = ApplicationStatus.COMPLETED;
    }

    public void markAsFailed() {
        this.status = ApplicationStatus.FAILED;
    }

    public void markAsCancelled() {
        this.status = ApplicationStatus.CANCELLED;
    }
}