package com.zerock.driveu.service;

import com.zerock.driveu.dto.ExamScheduleCreateDTO;
import com.zerock.driveu.dto.ExamScheduleViewDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface ExamScheduleService {

    int register(ExamScheduleCreateDTO dto);       // 등록 후 생성된 scheduleId반환

    // 월별 조회: 시험장 + 연/월 -> 날짜별 등록건수
    Map<LocalDate, Integer> getMonthlyCounts(Long testCenterId, int year, int month);

    // 주간 조회: 시험장 + 주 시작일 -> 그 주 일정 목록 (ViewDTO로 변환해서 반환)
    List<ExamScheduleViewDTO> getWeeklySchedules(Long testCenterId, LocalDate weekStart);

    // 슬롯 조회: 시험장 + 날짜 + 시간 -> 그 칸 일정 목록 (3단계 우측 패널)
    List<ExamScheduleViewDTO> getSlotSchedules(Long testCenterId, LocalDate examDate, LocalTime examTime);

    // 단건 삭제 (3단계 우측 패널에서 일정 하나 삭제)
    void delete(Long scheduleId);
}
