package com.zerock.driveu.service;

import com.zerock.driveu.dto.DriverLicenseExamPredictResponseDTO;

public interface DriverLicenseExamStatsService {

    // 월일시간별 가중치 적용해 데이터 가공
    DriverLicenseExamPredictResponseDTO predictCurrentExamStatus (String TestCenterName, int month, int day, int hour);

}
