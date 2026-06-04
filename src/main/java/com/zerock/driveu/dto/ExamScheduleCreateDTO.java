package com.zerock.driveu.dto;

import com.zerock.driveu.domain.enums.ExamType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduleCreateDTO {

    @NotNull(message = "시험 종류를 선택하세요.")
    private ExamType examType;          // 폼: 시험구분 (WRITTEN/FUNCTION/DRIVE)

    private String licenseType;         // 폼: 면허종별

    @NotNull(message = "시험장을 선택하세요.")
    private Long testCenterId;          // 폼: 시험장 (지역 select는 안 보냄, ID만)

    @NotNull(message = "시험 날짜를 입력하세요.")
    @Future(message = "시험 날짜는 미래여야 합니다.")
    private LocalDate examDate;         // 폼: 시험일

    @NotEmpty(message = "시험 시간을 1개 이상 선택하세요.")
    private List<LocalTime> examTimes;         // 폼: 시험시간

    @Min(value = 1, message = "정원은 1명 이상이어야 합니다.")
    private int maxCount;               // 폼: 정원
}