package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LicenseApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 신청 내역 pk

    // 로컬, 소셜 멤버의 seq 저장
    @Column(nullable = false)
    private Long userSeq;

    // 로컬, 소셜을 저장해서 중복 방지
    @Column(nullable = false)
    private String userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationType type; //신규, 재발급, 갱신 구별용

    // 기본값으로 "APPLIED"가 무조건 들어가도록 설정하여 '신청완료' 상태를 유지합니다.
    @Builder.Default
    @Column(nullable = false)
    private String status = "APPLIED";

    @Column(nullable = false)
    private String receiveLocation; // 수령 장소

    @Column(nullable = false)
    private LocalDate receiveDate;  //수령 희망 날짜

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 신청 일자

    // 신청 종류(신규/재발급/갱신) 구분용 Enum만 내부에 남겨둠
    public enum ApplicationType {
        NEW, REISSUE, RENEWAL
    }
}