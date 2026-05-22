package com.zerock.driveu.domain.enums;

public enum ApplicationStatus {

    WAITING_PAYMENT,        // 결제대기
    COMPLETED,              // 결제완료
    FAILED,                 // 결제실패
    CANCELLED               // 결제취소
}
