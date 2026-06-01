package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicensePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 결제 내역과 신청서를 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false) // FK
    private LicenseApplication licenseApplication;

    @Column(nullable = false)
    private String impUid; // 포트원 결제 고유 번호

    @Column(nullable = false, unique = true) // 중복결제 방지 고유번호
    private String merchantUid;

    @Column(nullable = false)
    private Long userSeq; // 결제한 사용자

    @Column(nullable = false)
    private String status; // 결제 상태

    @Column(nullable = false)
    private int amount; // 결제금액
}