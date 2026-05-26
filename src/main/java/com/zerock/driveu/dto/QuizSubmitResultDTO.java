package com.zerock.driveu.dto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitResultDTO {

    private boolean passed;
    private int totalCount;
    private int correctCount;
    private int wrongCount;
    private String message;
}