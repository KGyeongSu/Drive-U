package com.zerock.driveu.domain.enums;

import lombok.Getter;

@Getter
public enum ExamType {
    // TODO: 테스트 종료 후 실제 응시료로 복귀 (WRITTEN: 10000, SKILL: 22000, ROAD: 25000)
    WRITTEN(1000),        // 학과시험
    SKILL(1000),          // 기능시험
    ROAD(1000);            // 도로주행

    private final int fee;

    ExamType(int fee){
        this.fee = fee;
    }
}
