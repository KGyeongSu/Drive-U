package com.zerock.driveu.dto;

import com.zerock.driveu.domain.LicenseApplication.ApplicationType;
import lombok.Data;

@Data
public class LicenseDTO {
    private Long userSeq;
    private String userType;
    private ApplicationType type;
    private String receiveLocation;
    private String receiveDate; // 결제 완료 시 서버로 넘어오는 그 날짜값!
}