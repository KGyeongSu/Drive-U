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
    // 시험 종류
    public static final String EXAM_WRITTEN = "WRITTEN";
    public static final String EXAM_SKILL   = "SKILL";
    public static final String EXAM_ROAD    = "ROAD";
    // 시험 응시료
    public static final int FEE_WRITTEN = 10000;
    public static final int FEE_SKILL   = 22000;
    public static final int FEE_ROAD    = 25000;
    // 응시 종별
    public static final List<String> LICENSE_TYPES = List.of("1종 보통", "2종 보통");

    private ExamConstants() {}
}