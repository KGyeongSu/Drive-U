package com.zerock.driveu.service;

import com.zerock.driveu.dto.DriverLicenseExamPredictResponseDTO;
import com.zerock.driveu.repository.DriverLicenseExamStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverLicenseExamStatsServiceImpl implements DriverLicenseExamStatsService {

    private final DriverLicenseExamStatsRepository examRepository;
    private final DriverCenterMapService mapService;

    private static final double[][] EXAM_DAY_WEIGHTS = {

            // 월 화 수 목 금
            { 1.2, 0.8, 0.8, 1.1, 1.1 }, // 1월
            { 1.6, 1.5, 1.5, 1.7, 1.4 }, // 2월
            { 1.5, 1.4, 1.5, 1.5, 1.4 }, // 3월
            { 1.3, 1.0, 0.9, 0.9, 1.1 }, // 4월
            { 1.3, 1.2, 1.0, 0.9, 1.2 }, // 5월
            { 1.3, 1.1, 1.0, 1.2, 1.3 }, // 6월
            { 1.4, 1.3, 1.0, 1.3, 1.4 }, // 7월
            { 1.3, 1.3, 1.3, 1.3, 1.4 }, // 8월
            { 1.3, 1.0, 0.9, 1.0, 1.2 }, // 9월
            { 1.2, 0.8, 0.9, 1.1, 1.1 }, // 10월
            { 1.3, 1.0, 0.9, 1.0, 1.2 }, // 11월
            { 1.3, 1.3, 1.3, 1.3, 1.4 }  // 12월

    };

    private static final double [][] EXAM_HOUR_WEIGHTS = {

            // 9시   10시  11시  12시  13시  14시  15시  16시  17시
            { 1.0,  0.1,  0.1,  0.3,  1.5,  1.5,  0.5,  0.7,  0.7 }, // 1월
            { 2.2,  0.1,  0.1,  0.2,  0.3,  0.4,  0.4,  0.8,  0.8 }, // 2월
            { 2.3,  0.1,  0.1,  0.2,  0.2,  0.3,  0.2,  0.6,  0.6 }, // 3월
            { 1.2,  0.2,  0.1,  0.3,  1.4,  1.4,  0.9,  0.7,  0.5 }, // 4월
            { 1.2,  0.3,  0.1,  0.2,  1.0,  1.5,  1.5,  0.8,  0.6 }, // 5월
            { 1.4,  0.3,  0.1,  0.3,  0.9,  1.6,  1.6,  0.7,  0.5 }, // 6월
            { 2.2,  0.2,  0.1,  0.2,  1.0,  1.5,  1.5,  0.8,  0.6 }, // 7월
            { 2.2,  0.2,  0.1,  0.2,  1.0,  1.5,  1.5,  0.8,  0.6 }, // 8월
            { 1.2,  0.2,  0.1,  0.4,  1.4,  1.1,  0.6,  0.6,  0.4 }, // 9월  (오후 피크)
            { 1.0,  0.1,  0.1,  0.3,  1.4,  1.2,  0.4,  0.4,  0.4 }, // 10월 (안정기)
            { 1.2,  0.2,  0.1,  0.3,  1.5,  1.1,  0.5,  0.5,  0.4 }, // 11월 (피크 전 빌드업)
            { 2.2,  0.2,  0.1,  0.2,  1.0,  1.5,  1.5,  0.8,  0.6 }  // 12월

    };

    @Override
    public DriverLicenseExamPredictResponseDTO predictCurrentExamStatus (String testCenterName, int month, int day, int hour) {

        long totalCnt = examRepository.find3CntByTestCenterName(testCenterName);

        // 요일 인덱스 계산
        int currentYear = LocalDateTime.now().getYear();
        LocalDate targetDate = LocalDate.of(currentYear, month, day);

        // 1~7, 인덱스로 하면 0~6
        int dayOfWeekIndex = targetDate.getDayOfWeek().getValue() - 1;

        // 영업시간 외
        if (hour < 9 || hour > 17) {

            return DriverLicenseExamPredictResponseDTO.builder()
                    .testCenterName(testCenterName)
                    .selectedDate(LocalDateTime.now())
                    .statusColor("GRAY")
                    .expectedMinutes(-1)
                    .totalCnt(totalCnt)
                    .expectedWaitCount(0)
                    .build();

        }

        // 주말에는 영업마감 처리
        if (dayOfWeekIndex >= 5) {

            return DriverLicenseExamPredictResponseDTO.builder()
                    .testCenterName(testCenterName)
                    .selectedDate(LocalDateTime.now())
                    .statusColor("GRAY")
                    .expectedMinutes(-1)
                    .totalCnt(totalCnt)
                    .expectedWaitCount(0)
                    .build();

        }

        // 가중치 추출
        double dayWeight = EXAM_DAY_WEIGHTS[month - 1][dayOfWeekIndex];
        double hourWeight = EXAM_HOUR_WEIGHTS[month - 1][hour - 9];
        String cityGroup = mapService.getCityGroup(testCenterName);

        // 시험장 가중치 계산
        double examCenterWeight = (double) totalCnt / 75000.0;
        if (examCenterWeight < 0.3) examCenterWeight = 0.3;

        // 대기 예상 시간 & 대기 인수 (대기인수 가공_totalCnt 기반으로 역산)
        double baseMinutes = 20.0;
        double divisor = 1800.0;
        if("수도권".equals(cityGroup)) {

            divisor = 5000.0;

        }

        int finalExamTime = (int) (baseMinutes * dayWeight * hourWeight * examCenterWeight);
        int expectedWaitCount = (int) Math.ceil((totalCnt / divisor) * (finalExamTime) / baseMinutes);
        if (expectedWaitCount <= 0 && finalExamTime > 0) {

            expectedWaitCount = 1;

        }

        // 혼잡도 색상 결정
        String statusColor = "GREEN";
        if (finalExamTime >= 70) {

            statusColor = "RED";

        } else if (finalExamTime >= 30) {

            statusColor = "YELLOW";

        }

        return DriverLicenseExamPredictResponseDTO.builder()
                .testCenterName(testCenterName)
                .selectedDate(LocalDateTime.now())
                .expectedMinutes(finalExamTime)
                .statusColor(statusColor)
                .totalCnt(totalCnt)
                .expectedWaitCount(expectedWaitCount)
                .build();

    }

}
