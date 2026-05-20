package com.zerock.driveu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLicenseIssuePredictResponseDTO {

    // 홈페이지 접속 시간 기준
    private LocalDateTime selectedDate;

    // DB에서 대기시간 및 대기인원 정보 가공에 필요한 data 담는 목적
    private long totalCnt;

    // 출력할 정보 담는 목적
    private String testCenterName;
    private String statusColor;
    private int expectedWaitCount;
    private int expectedMinutes;

}
