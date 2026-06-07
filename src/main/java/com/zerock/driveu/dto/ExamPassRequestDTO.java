package com.zerock.driveu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExamPassRequestDTO {

    // 어떤 신청건을 합격처리하는지
    @NotNull
    private Long applicationId;

    // 관리자가 기입한 점수
    @NotNull
    private Long score;
}