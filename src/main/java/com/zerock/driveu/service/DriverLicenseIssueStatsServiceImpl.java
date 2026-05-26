package com.zerock.driveu.service;

import com.zerock.driveu.dto.DriverLicenseIssuePredictResponseDTO;
import com.zerock.driveu.repository.DriverLicenseIssueStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverLicenseIssueStatsServiceImpl implements DriverLicenseIssueStatsService {

    private final DriverLicenseIssueStatsRepository issueRepository;
    private final DriverCenterMapService mapService;

    private static final int BASE_MINUTES = 15;

    private static final double [][] ISSUE_DAY_WEIGHTS = {

            // 월 화 수 목 금
            { 1.35, 0.95, 0.90, 0.95, 1.25 }, // 1월
            { 1.45, 1.35, 1.10, 1.20, 1.25 }, // 2월
            { 1.25, 1.05, 0.95, 1.00, 1.15 }, // 3월
            { 1.10, 0.95, 0.85, 0.90, 1.00 }, // 4월
            { 1.30, 1.05, 0.90, 1.00, 1.25 }, // 5월
            { 1.20, 0.95, 0.90, 0.90, 1.10 }, // 6월
            { 1.35, 1.05, 1.00, 1.05, 1.30 }, // 7월
            { 1.55, 1.30, 1.15, 1.20, 1.45 }, // 8월
            { 1.20, 1.00, 0.85, 0.90, 1.05 }, // 9월
            { 1.15, 0.95, 0.90, 0.90, 1.05 }, // 10월
            { 1.25, 1.05, 0.95, 1.00, 1.20 }, // 11월
            { 1.60, 1.40, 1.25, 1.35, 1.55 }  // 12월

    };

    private static final double [][] ISSUE_HOUR_WEIGHTS = {

            // 9시   10시  11시  12시  13시  14시  15시  16시  17시
            { 2.30, 0.10, 0.35, 0.60, 0.75, 0.90, 0.85, 1.65, 1.70 }, // 1월
            { 4.10, 0.15, 0.45, 0.55, 0.65, 0.50, 0.25, 1.10, 1.05 }, // 2월
            { 2.15, 0.15, 0.40, 0.45, 0.60, 0.65, 0.35, 1.45, 1.55 }, // 3월
            { 1.95, 0.10, 0.25, 0.20, 0.45, 0.60, 0.20, 1.25, 1.35 }, // 4월
            { 2.20, 0.15, 0.40, 0.50, 0.65, 1.40, 1.30, 1.50, 1.65 }, // 5월
            { 2.10, 0.10, 0.20, 0.30, 0.40, 0.55, 0.45, 1.30, 1.35 }, // 6월
            { 2.25, 0.15, 0.30, 0.40, 0.70, 0.65, 0.60, 1.55, 1.65 }, // 7월
            { 3.85, 0.25, 0.50, 0.65, 0.80, 1.45, 1.50, 1.85, 1.90 }, // 8월
            { 2.15, 0.10, 0.25, 0.30, 0.40, 0.55, 0.35, 1.35, 1.40 }, // 9월
            { 2.05, 0.10, 0.20, 0.25, 0.35, 0.50, 0.40, 1.25, 1.35 }, // 10월
            { 2.20, 0.15, 0.35, 0.45, 0.65, 1.30, 1.25, 1.55, 1.65 }, // 11월
            { 3.90, 0.30, 0.55, 0.70, 1.95, 2.10, 1.90, 1.45, 1.50 }  // 12월

    };

    @Override
    public DriverLicenseIssuePredictResponseDTO predictCurrentIssueStatus (String testCenterName, int month, int day, int hour) {

        long issueCount = issueRepository.find3CntByTestCenterName(testCenterName);

        // 요일 인덱스 계산
        int currentYear = LocalDateTime.now().getYear();
        LocalDate targetDate = LocalDate.of(currentYear, month, day);

        // 1~7, 인덱스 > 0~6
        int dayOfWeekIndex = targetDate.getDayOfWeek().getValue() - 1;

        // 영업시간 외
        if (hour < 9 || hour > 17) {

            return DriverLicenseIssuePredictResponseDTO.builder()
                    .testCenterName(testCenterName)
                    .selectedDate(LocalDateTime.now())
                    .statusColor("GRAY")
                    .expectedMinutes(-1)
                    .totalCnt(issueCount)
                    .expectedWaitCount(0)
                    .build();

        }

        // 주말에는 운영마감 처리
        if (dayOfWeekIndex >= 5) {

            return DriverLicenseIssuePredictResponseDTO.builder()
                    .testCenterName(testCenterName)
                    .selectedDate(LocalDateTime.now())
                    .statusColor("GRAY")
                    .expectedMinutes(-1)
                    .totalCnt(issueCount)
                    .expectedWaitCount(0)
                    .build();

        }

        // 가중치 추출
        double dayWeight = ISSUE_DAY_WEIGHTS[month - 1][dayOfWeekIndex];
        double hourWeight = ISSUE_HOUR_WEIGHTS[month - 1][hour - 9];
        String cityGroup = mapService.getCityGroup(testCenterName);

        // 시험장 가중치 계산
        double issueCenterWeight = (double) issueCount / 85000.0;
        if (issueCenterWeight < 0.3) issueCenterWeight = 0.3;

        // 대기 예상시간 계산 & 대기 인수 (issueCount 기반으로 역산)
        double divisor = 2000.0;
        if ("수도권".equals(cityGroup)) {

            divisor = 6000.0;

        }

        int finalIssueTime = (int) (BASE_MINUTES * dayWeight * hourWeight * issueCenterWeight);
        int expectedWaitCount = (int) Math.ceil(((double) issueCount / divisor) * ((double) finalIssueTime / BASE_MINUTES));
        if (expectedWaitCount <= 0 && finalIssueTime > 0) {

            expectedWaitCount = 1;

        }

        // 혼잡도 색상 결정
        String statusColor = "GREEN";
        if (finalIssueTime >= 70) {

            statusColor = "RED";

        } else if (finalIssueTime >= 30) {

            statusColor = "YELLOW";

        }

        return DriverLicenseIssuePredictResponseDTO.builder()
                .testCenterName(testCenterName)
                .selectedDate(LocalDateTime.now())
                .expectedMinutes(finalIssueTime)
                .statusColor(statusColor)
                .totalCnt(issueCount)
                .expectedWaitCount(expectedWaitCount)
                .build();

    }

}
