package com.zerock.driveu.domain;

import com.zerock.driveu.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    // 신청건과 N:1 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(nullable = false, length = 50)
    private String merchantUid;         // 주문번호 (Application에도 있지만 결제 식별 편의상 중복)

    @Column(nullable = false)
    private int amount;                 // 실제 결제 금액 (포트원 응답 기준)

    @Column(length = 30)
    private String payMethod;           // 카드/계좌이체/카카오페이 등

    @Column(length = 50)
    private String pgProvider;          // PG사 (KG이니시스 등)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column
    private LocalDateTime paidAt;       // 결제 완료 시각 (포트원에서 받음)

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
    public void markAsPaid(LocalDateTime paidAt) {
        this.status = PaymentStatus.PAID;
        this.paidAt = paidAt;
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markAsCancelled() {
        this.status = PaymentStatus.CANCELLED;
    }
}