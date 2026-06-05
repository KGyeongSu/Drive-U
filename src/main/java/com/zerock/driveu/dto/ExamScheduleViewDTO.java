package com.zerock.driveu.dto;

import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.enums.ExamType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ExamScheduleViewDTO {

    private Long scheduleId;        // ★ 수정/삭제 시 어느 일정인지 식별 (PK)
    private ExamType examType;      // WRITTEN / FUNCTION / DRIVE
    private String licenseType;     // 학과면 null
    private LocalDate examDate;
    private LocalTime examTime;
    private int maxCount;           // 정원

    // Entity → DTO (testCenter 같은 LAZY 연관은 안 꺼냄 → 직렬화 안전)
    public static ExamScheduleViewDTO from(ExamSchedule e) {
        return ExamScheduleViewDTO.builder()
                .scheduleId(e.getScheduleId())
                .examType(e.getExamType())
                .licenseType(e.getLicenseType())
                .examDate(e.getExamDate())
                .examTime(e.getExamTime())
                .maxCount(e.getMaxCount())
                .build();
    }
}