package com.zerock.driveu.domain.enums;

import lombok.Getter;

@Getter
public enum ExamType {
    WRITTEN(1000, "학과시험"),        // 학과시험
    FUNCTION(1000, "기능시험"),       // 기능시험
    DRIVE(1000, "도로주행");          // 도로주행

    private final int fee;
    private final String label;

    ExamType(int fee, String label){
        this.label = label;
        this.fee = fee;
    }

}
