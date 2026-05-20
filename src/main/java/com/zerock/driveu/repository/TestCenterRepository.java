package com.zerock.driveu.repository;

import com.zerock.driveu.entity.TestCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestCenterRepository extends JpaRepository<TestCenter, Long> {

    List<TestCenter> findByRegion(String region);

    @Query("SELECT DISTINCT t.region FROM TestCenter t")
    List<String> findDistinctRegions();
}
