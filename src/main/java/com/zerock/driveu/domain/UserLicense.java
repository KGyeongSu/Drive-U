package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_license") // DB 테이블명 명시
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 필수
public class UserLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userSeq;

    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;

    private String licenseNumber;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private LicenseStatus status;

    //면허종류
    public enum LicenseType {
        TYPE_1_NORMAL, // 1종 보통
        TYPE_2_AUTO,   // 2종 자동
        MOTORCYCLE     // 오토바이
    }

    //유효기간 상태값
    public enum LicenseStatus {
        VALID,    // 유효
        EXPIRED,  // 만료
        SUSPENDED // 정지
    }
}

