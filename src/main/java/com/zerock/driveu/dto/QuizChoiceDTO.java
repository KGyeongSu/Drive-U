package com.zerock.driveu.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizChoiceDTO {

    private Long choiceId;
    private String choiceText;
    private String correctYn;
}