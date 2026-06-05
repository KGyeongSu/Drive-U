package com.zerock.driveu.domain.enums;

import lombok.Getter;

@Getter
public enum ExamType {
    WRITTEN(1000, "학과시험"),   // 학과: 합격선이 종별로 다름(1종 70 / 2종 60) → 메서드에서 분기
    FUNCTION(1000, "기능시험"),  // 기능: 종별 무관 80
    DRIVE(1000, "도로주행");     // 도로주행: 종별 무관 70

    private final int fee;
    private final String label;

    ExamType(int fee, String label){
        this.label = label;
        this.fee = fee;
    }

    // 시험·종별에 맞는 합격선 반환
    public int passLineFor(String licenseType) {
        return switch (this) {
            case WRITTEN  -> "1종 보통".equals(licenseType) ? 70 : 60;  // 학과만 종별 분기
            case FUNCTION -> 80;                                        // 기능 고정
            case DRIVE    -> 70;                                        // 도로주행 고정
        };
    }

    // 이 점수가 합격인지 판정
    public boolean isPassed(String licenseType, long score) {
        return score >= passLineFor(licenseType);
    }
}
