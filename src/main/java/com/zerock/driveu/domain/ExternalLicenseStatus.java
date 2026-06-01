package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExternalLicenseStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 가상 공단 데이터의 고유 번호(PK)

    //어떤 유저의 합격 데이터인지 식별하는 복합 키 세트(로컬 , 소셜)
    @Column(nullable = false)
    private Long userSeq;

    @Column(nullable = false)
    private String userType; // 로컬 소셜 타입 구분용

    @Column(nullable = false)
    private String licenseType; // 발급/갱신/재발급 구분용

    //합격 여부
    @Column(nullable = false)
    private boolean writtenPassed;   // 필기시험 합격 여부

    @Column(nullable = false)
    private boolean functionPassed;  // 기능시험 합격 여부

    @Column(nullable = false)
    private boolean drivingPassed;   // 도로주행시험 합격 여부
}