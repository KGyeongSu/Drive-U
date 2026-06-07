package com.zerock.driveu.dto;

import com.zerock.driveu.domain.Application;
import com.zerock.driveu.domain.ExamSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ExamCandidateDTO {

    private Long applicationId;     // 합격처리용 식별자
    private String examType;        // 시험구분 (라벨)
    private String licenseType;     // 면허종별
    private String contactName;     // 응시생 이름 (테이블조회)
    private String contactPhone;
    private String contactEmail;
    private Long examPassId;        // 합격행pk, 합격 안 했으면 null
    private Long examFailId;        // 불합격행pk, 불합격 안 했으면 null

    // ── 필터 기준 ──
    private String centerName;      // 시험장
    private LocalDate examDate;     // 시험일
    private LocalTime examTime;     // 시간대

    public static ExamCandidateDTO from(Application app, Long examPassId, Long examFailId, String contactName) {
        ExamSchedule schedule = app.getExamSchedule();
        return ExamCandidateDTO.builder()
                .applicationId(app.getApplicationId())
                .examPassId(examPassId)
                .examFailId(examFailId)
                .contactName(contactName)
                .examType(app.getExamType().getLabel())
                .licenseType(app.getLicenseType())
                .contactPhone(app.getContactPhone())
                .contactEmail(app.getContactEmail())
                .centerName(schedule.getTestCenter().getCenterName())
                .examDate(schedule.getExamDate())
                .examTime(schedule.getExamTime())
                .build();
    }
}