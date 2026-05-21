package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="driver_license_exam_stats")
public class DriverLicenseExamStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_license_exam_stats_id")
    private Long id;

    @Column(name = "license_exam_year", nullable = false)
    private Integer year;

    @Column(name = "test_center_name", nullable = false, length = 50)
    private String testCenterName;

    @Column(name = "department_cnt", columnDefinition = "int default 0")
    private Integer departmentCnt;

    @Column(name = "function_cnt", columnDefinition = "int default 0")
    private Integer functionCnt;

    @Column(name = "road_driving_cnt", columnDefinition = "int default 0")
    private Integer roadDrivingCnt;

    @Column(

            name = "total_cnt",
            insertable = false,
            updatable = false,
            columnDefinition = "int GENERATED ALWAYS AS (department_cnt + function_cnt + road_driving_cnt) STORED"

    )
    private Integer totalCnt;

    }
