package com.zerock.driveu.repository;

import com.zerock.driveu.entity.ExamSchedule;
import com.zerock.driveu.entity.TestCenter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {

    // 시험 종류로 조회 (학과, 기능, 운전면허)
    List<ExamSchedule> findByExamType(String examType);

    // 시험 종류 + 시험장으로 조회
    List<ExamSchedule> findByExamTypeAndTestCenter(String examType, TestCenter testCenter);

    // 시험 종류 + 시험장 + 날짜로 조회 (캘린더에서 날짜 클릭 시)
    List<ExamSchedule> findByExamTypeAndTestCenterAndExamDate(
            String examType, TestCenter testCenter, LocalDate examDate);



}