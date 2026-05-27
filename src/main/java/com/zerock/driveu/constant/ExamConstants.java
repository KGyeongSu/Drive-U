package com.zerock.driveu.constant;

import java.time.LocalTime;
import java.util.List;

//상수 클래스
public class ExamConstants {

    // 시험시간
    public static final List<LocalTime> EXAM_TIMES = List.of(
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            LocalTime.of(16, 0)
    );

    // 학과시험 응시 종별
    public static final List<String> WRITTEN_LICENSE_TYPES = List.of(
            "1종 보통",
            "2종 보통"
    );

    // 기능시험 응시 종별
    public static final List<String> FUNCTION_LICENSE_TYPES = List.of(
            "1종 대형",
            "1종 보통",
            "2종 보통",
            "2종 소형"
    );

//    // 도로주행 응시 종별 (추후 추가)
//    public static final List<String> DRIVE_LICENSE_TYPES = List.of()

    private ExamConstants() {}
}