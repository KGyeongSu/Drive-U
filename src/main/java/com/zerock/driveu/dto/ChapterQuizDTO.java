package com.zerock.driveu.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterQuizDTO {

    private Long quizId;
    private String questionText;
    private String explanation;
    private List<QuizChoiceDTO> choices;
}