package com.zerock.driveu.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "driver_license_issue_stats")
public class DriverLicenseIssueStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_license_issue_stats_id")
    private Long id;

    @Column(name = "license_issue_year", nullable = false)
    private Integer year;

    @Column(name = "test_center_name", nullable = false)
    private String testCenterName;

    @Column(name = "new_issue_cnt", columnDefinition = "int default 0")
    private Integer newIssueCnt;

    @Column(name = "re_issue_cnt", columnDefinition = "int default 0")
    private Integer reIssueCnt;

    @Column(name = "renewal_cnt", columnDefinition = "int default 0")
    private Integer renewalCnt;

    @Column(

            name = "total_issue_cnt",
            insertable = false,
            updatable = false,
            columnDefinition = "int GENERATED ALWAYS AS (new_issue_cnt + re_issue_cnt + renewal_cnt) STORED"

    )
    private Integer totalIssueCnt;

}
