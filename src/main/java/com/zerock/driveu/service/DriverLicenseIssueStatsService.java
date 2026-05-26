package com.zerock.driveu.service;


import com.zerock.driveu.dto.DriverLicenseIssuePredictResponseDTO;

public interface DriverLicenseIssueStatsService {

    DriverLicenseIssuePredictResponseDTO predictCurrentIssueStatus (String testCenterName, int month, int day, int hour);

}
