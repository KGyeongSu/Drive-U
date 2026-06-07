package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "practice_license")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PracticeLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long practiceLicenseId;

    // 회원 식별 (Application과 동일하게 seq + type 조합)
    @Column(nullable = false)
    private Long userSeq;
    @Column(nullable = false, length = 20)
    private String memberType;          // "MEMBER" / "SOCIAL"


    @Column(nullable = false, length = 30)
    private String licenseType;         // "1종 보통", "2종 보통"

    @Column(nullable = false)
    private int fee;                    // 발급 수수료

    // ── 결제 검증용 (Application과 동일 컨셉) ──
    @Column(nullable = false, length = 20)
    private String status;              // "WAITING_PAYMENT" / "ISSUED" / "FAILED" / "CANCELLED"

    @Column(nullable = false, length = 50, unique = true)
    private String merchantUid;         // 포트원 주문번호 (검증용)

    // ── 발급 결과 (검증 성공 시점에 박힘) ──
    @Column(length = 30)
    private String licenseNumber;       // 연습면허 번호 (발급 시 생성)

    @Column
    private LocalDate issuedDate;       // 발급일
    @Column
    private LocalDate expiryDate;       // 만료일 (발급일 + 1년)

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

    // ── 상태 변경 메서드 (setter 대신) ──

    // 검증 성공 → 발급 확정 (번호/발급일/유효기간 한 번에 박음)
    public void markAsIssued(String licenseNumber, LocalDate issuedDate) {
        this.status = "ISSUED";
        this.licenseNumber = licenseNumber;
        this.issuedDate = issuedDate;
        this.expiryDate = issuedDate.plusYears(1);   // 발급일 + 1년
    }

    public void markAsFailed() {
        this.status = "FAILED";
    }

    public void markAsCancelled() {
        this.status = "CANCELLED";
    }
}