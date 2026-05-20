package com.zerock.driveu.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApplySessionDTO {

    private String examType;        // "WRITTEN" / "SKILL" / "ROAD"
    private String licenseType;     // 응시 종별 (1종보통 등)
    private Long testCenterId;      // 시험장 PK (FK)
    private LocalDate examDate;     // 시험 날짜
    private String examTime;        // 시험 시간
    private Long examScheduleId;    // ExamSchedule PK (FK)

    private String contactPhone;    // wApply3에서 추가
    private String contactEmail;    // wApply3에서 추가
}