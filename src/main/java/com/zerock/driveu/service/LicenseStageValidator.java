package com.zerock.driveu.service;

import com.zerock.driveu.domain.enums.ExamType;

public interface LicenseStageValidator {

    // 판단용 (로드맵 활성화 + 차단 양쪽에서 재사용)
    boolean canApply(Long userSeq, String memberType, ExamType examType);

    // 자격 없으면 예외 -> Apply 진입 차단용
    void validate(Long userSeq, String memberType, ExamType examType);
}