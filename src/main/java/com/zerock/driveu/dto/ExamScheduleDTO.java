package com.zerock.driveu.dto;

import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.enums.ExamType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamScheduleDTO {

    private Long scheduleId;
    private ExamType examType;
    private String testCenterName;   // 시험장 이름 (TestCenter.centerName)
    private String region;           // 지역 (TestCenter.region)
    private LocalDate examDate;
    private LocalTime examTime;
    private int maxCount;
    private Long currentCount;

    // Entity → DTO 변환
    public static ExamScheduleDTO from(ExamSchedule entity, long currentCount) {
        return ExamScheduleDTO.builder()
                .scheduleId(entity.getScheduleId())
                .examType(entity.getExamType())
                .testCenterName(entity.getTestCenter().getCenterName())
                .region(entity.getTestCenter().getRegion())
                .examDate(entity.getExamDate())
                .examTime(entity.getExamTime())
                .maxCount(entity.getMaxCount())
                .currentCount(currentCount)   // DB에 count쿼리로 조회해서 DB컬럼으로x
                .build();
    }
}