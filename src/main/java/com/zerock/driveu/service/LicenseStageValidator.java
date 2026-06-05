package com.zerock.driveu.service;

import com.zerock.driveu.domain.enums.ExamType;

public interface LicenseStageValidator {

    // ── GET용 : 종별 무관 (직전 단계를 '아무 종별이라도' 통과했나) ──
    boolean canEnter(Long userSeq, String memberType, ExamType examType);

    // ── POST용 : 종별 일치 (고른 종별로 직전 단계를 통과했나) ──
    boolean canApply(Long userSeq, String memberType, ExamType examType, String licenseType);

    // ── 연습면허 (ExamType 밖) ──
    boolean canEnterPracticeLicense(Long userSeq, String memberType);                          // GET용
    boolean canIssuePracticeLicense(Long userSeq, String memberType, String licenseType);      // POST용
}