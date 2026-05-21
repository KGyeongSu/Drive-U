package com.zerock.driveu.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerSubmitDTO {

    private Long quizId;
    private Long choiceId;
}