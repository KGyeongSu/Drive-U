package com.zerock.driveu.domain.enums;

import lombok.Getter;

@Getter
public enum ExamType {
    WRITTEN(1000),        // 학과시험
    FUNCTION(1000),       // 기능시험
    DRIVE(1000);          // 도로주행

    private final int fee;

    ExamType(int fee){
        this.fee = fee;
    }
}
