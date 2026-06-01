package com.zerock.driveu.service;

import com.zerock.driveu.domain.LicenseApplication;
import com.zerock.driveu.domain.LicenseApplication.ApplicationType;
import com.zerock.driveu.dto.LicenseDTO;

public interface LicenseService {

    // 필기, 기능, 도로주행 모두 합격했는지 select 하는 메소드
    boolean checkLicenseEligibility(Long userSeq, String userType);

    //  결제 완료 후 최종 신청 데이터를 DB에 insert하는 메소드
    LicenseApplication registerApplication(Long userSeq, String userType, ApplicationType type, String receiveLocation, String receiveDate);

    LicenseApplication registerApplication(LicenseDTO dto);

    // 결제 완료 후 결제 정보를 DB에 insert하는 메소드
    void savePayment(Long userSeq, String merchantUid, String impUid, int amount, Long applicationId);

    boolean isAlreadyApplied(Long userSeq, ApplicationType type);
}
