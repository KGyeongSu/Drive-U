package com.zerock.driveu.repository;

import com.zerock.driveu.domain.ExamSchedule;
import com.zerock.driveu.domain.TestCenter;
import com.zerock.driveu.domain.enums.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
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



}