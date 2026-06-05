package com.zerock.driveu.repository;

import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.TestCenter;
import com.zerock.driveu.domain.enums.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {

    // 시험 종류로 조회 (학과, 기능, 운전면허)
    List<ExamSchedule> findByExamType(ExamType examType);

    // 시험 종류 + 시험장으로 조회
    List<ExamSchedule> findByExamTypeAndTestCenter(ExamType examType, TestCenter testCenter);

    // 시험 종류 + 시험장 + 날짜로 조회 (캘린더에서 날짜 클릭 시) 학과시험
    List<ExamSchedule> findByExamTypeAndTestCenterAndExamDate(
            ExamType examType, TestCenter testCenter, LocalDate examDate);

    // 시험 종류 + 라이센스 종류 + 시험장 + 날짜로 조회 (캘린더에서 날짜 클릭 시) 기능시험
    List<ExamSchedule> findByExamTypeAndLicenseTypeAndTestCenterAndExamDate(
            ExamType examType, String licenseType, TestCenter testCenter, LocalDate examDate);

    // 시험장 + 날짜 범위로 조회 (월별 조회: 1일~말일)
    List<ExamSchedule> findByTestCenterAndExamDateBetween(
            TestCenter testCenter, LocalDate startDate, LocalDate endDate);

    // 슬롯 단위 조회: 시험장 + 날짜 + 시간 -> 그 칸의 일정 목록 (examSchedule 3단계 우측 패널)
    List<ExamSchedule> findByTestCenterAndExamDateAndExamTime(
            TestCenter testCenter, LocalDate examDate, LocalTime examTime);

    // licenseType 있는 경우 (FUNCTION/DRIVE)
    boolean existsByExamTypeAndLicenseTypeAndTestCenterAndExamDateAndExamTime(
            ExamType examType, String licenseType, TestCenter testCenter,
            LocalDate examDate, LocalTime examTime);

    // licenseType 없는 경우 (WRITTEN - null 비교)
    boolean existsByExamTypeAndLicenseTypeIsNullAndTestCenterAndExamDateAndExamTime(
            ExamType examType, TestCenter testCenter,
            LocalDate examDate, LocalTime examTime);

}