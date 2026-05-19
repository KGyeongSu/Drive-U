package com.zerock.driveu.repository;

import com.zerock.driveu.domain.DriverLicenseIssueStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverLicenseIssueStatsRepository extends JpaRepository<DriverLicenseIssueStats, Long> {

    @Query("SELECT COALESCE(SUM(i.totalIssueCnt), 0) FROM DriverLicenseIssueStats i WHERE i.testCenterName = :testCenterName")
    long find3CntByTestCenterName (String testCenterName);

}
