package com.zerock.driveu.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbtSubmitResultDTO {

    private int totalCount;
    private int correctCount;
    private int wrongCount;
    private int score;
    private String passYn;

    private List<CbtQuestionResultDTO> questionResults;
}