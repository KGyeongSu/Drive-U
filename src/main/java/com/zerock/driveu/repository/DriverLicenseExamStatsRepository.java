package com.zerock.driveu.repository;

import com.zerock.driveu.domain.DriverLicenseExamStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverLicenseExamStatsRepository extends JpaRepository <DriverLicenseExamStats, Long> {

    // 시험장 distinct로 뽑아오기
    @Query("SELECT DISTINCT e.testCenterName FROM DriverLicenseExamStats e")
    List<String> findTestCenters ();

    // 3년간 시험장별 totalCnt 합산 & 결과가 null인 경우 대비해 기본값 0L (coalesce처리)
    @Query("SELECT COALESCE(SUM(e.totalCnt), 0) FROM DriverLicenseExamStats e WHERE e.testCenterName = :testCenterName")
    long find3CntByTestCenterName(@Param("testCenterName") String testCenterName);

}
